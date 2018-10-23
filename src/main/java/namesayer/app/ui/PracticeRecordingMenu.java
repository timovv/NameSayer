package namesayer.app.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import namesayer.app.Constants;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.AttemptInfo;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Menu where users can practice pronouncing names in the database.
 */
public class PracticeRecordingMenu extends StackPane {

    /**
     * timeline to keep track of the microphone level
     */
    private final Timeline micLevelTimeline;

    /**
     * Main menu which is returned to when the practice session is complete
     */
    private final Parent mainMenu;

    /**
     * Shop, used to give the user coins when they complete their practice session
     */
    private final NameSayerShop shop;

    /**
     * The database from which the recordings are coming from; used to source attempts and save new attempts
     */
    private NameSayerDatabase database;

    /**
     * The audio system to make and play recordings
     */
    private AudioSystem audioSystem;

    /**
     * The practice menu; returned to when the back button is pressed.
     */
    private Parent previous;

    /**
     * The total number of names being practiced.
     */
    private int total;

    /**
     * The current name being practiced
     */
    private ObjectProperty<List<Name>> current;

    /**
     * A list of the names which still need to be practiced
     */
    private LinkedList<List<Name>> remainingNames;

    /**
     * Scroll pane which contains the users' previous attmempts
     */
    private ScrollPane scrollPane = new ScrollPane();

    /**
     * View of the users' attempts
     */
    private AttemptView attemptView;

    /**
     * StackPane parent used to show JFXDialogs
     */
    @FXML
    private StackPane stackPane;

    /**
     * Widget which is used to make recordings
     */
    @FXML
    private RecordingWidget recordingWidget;

    /**
     * Label showing how many recordings have been made and how many are left
     */
    @FXML
    private Label countLabel;

    /**
     * Label showing the current name being practiced
     */
    @FXML
    private Label nameLabel;

    /**
     * Button which, when clicked, sends the user to the next name (or finishes the session if there are no names left)
     */
    @FXML
    private Button nextButton;

    /**
     * VBox used for the content
     */
    @FXML
    private VBox contentVBox;

    /**
     * Progress bar to show the user their current microphone level
     */
    @FXML
    private ProgressBar micLevelBar;

    /**
     * Create a new PracticeRecordingMenu with the given parameters.
     * @param previous The practice menu which will be returned to when the 'back' button is pressed
     * @param mainMenu The main menu which will be navigated to when the user completes their practice session
     * @param audioSystem The NameSayer audio system, used to make and save recordings
     * @param db The database used to add and save attempts
     * @param shop The shop used to give the user coins when they complete their practice session
     * @param names The list of names being practiced in this practice session.
     */
    public PracticeRecordingMenu(Parent previous,
                                 Parent mainMenu,
                                 AudioSystem audioSystem,
                                 NameSayerDatabase db,
                                 NameSayerShop shop,
                                 List<List<Name>> names) {
        this.mainMenu = mainMenu;

        this.audioSystem = audioSystem;
        this.previous = previous;
        this.database = db;
        this.shop = shop;
        this.remainingNames = new LinkedList<>(names);
        this.total = names.size();
        current = new SimpleObjectProperty<>(remainingNames.pollFirst());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/practiceRecordingMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }
        micLevelTimeline = new Timeline(new KeyFrame(Duration.millis(50),
                a -> {
                    double value = (1 - (audioSystem.getInputLevel() / -100));
                    value = (value >= 0) ? value : 0; // if value is negative, round it up to zero
                    micLevelBar.setProgress(value);
                })); // nominate -100dB as our 0 reference since it seems to work
        micLevelTimeline.setCycleCount(Animation.INDEFINITE);
        micLevelTimeline.play();
        // change these labels when the current name changes
        nameLabel.textProperty().bind(Bindings.createStringBinding(() ->
                Util.toTitleCase(String.join(" ", current.get().stream().map(Name::getName).collect(Collectors.toList()))), current));

        // show the number of recordings they have left
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> "(" + (total - remainingNames.size()) + "/" + total + ")", current));

        // change the button if it's the last recording
        nextButton.textProperty().bind(Bindings.when(Bindings.createBooleanBinding(remainingNames::isEmpty, current)).then("Finish").otherwise("Next"));
    }

    /**
     * Initialise the PracticeRecordingMenu's components
     */
    @FXML
    private void initialize() {

        recordingWidget.setAudioSystem(audioSystem);

        scrollPane.setFitToWidth(true);
        attemptView = new AttemptView(current.get(), database, this);
        scrollPane.setContent(attemptView);
        contentVBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // if the name changes, also update the attempts
        // when the recording is saved, create a new attempt
        recordingWidget.setOnSaveClicked(() -> {
            List<String> names = current.get().stream().map(Name::getName).collect(Collectors.toList());
            AttemptInfo attemptInfo = new AttemptInfo(names, LocalDateTime.now());
            database.getAttemptDatabase().createNew(attemptInfo, recordingWidget.getRecording());
        });
    }

    /**
     * Handles the back button being clicked. The user is sent back to the practice menu.
     */
    @FXML
    private void onBackClicked() {
        if (recordingWidget.isRecording()) {
            recordingWidget.stopRecording();
        }

        getScene().setRoot(previous);
    }

    /**
     * Plays the database name when the play button is clicked
     */
    @FXML
    private void onPlayClicked() {
        playName();
    }

    /**
     * @return A CompletableFuture object which returns when the entire name (concatenated audio) has been played
     */
    private CompletableFuture<Void> playName() {
        CompletableFuture<Void> next = CompletableFuture.completedFuture(null);
        for (Name name : current.get()) {
            next = next.thenCompose(x -> name.getRecording()).thenCompose(AudioClip::play);
        }

        return next;
    }

    /**
     * The compare button plays the database name, followed by the recorded name three times so that users can compare
     * their rendition with the database copy
     */
    @FXML
    private void onCompareClicked() {
        if(recordingWidget.getRecording() == null) {
            new JFXDialogHelper("You haven't made a recording", "You need to make a recording to compare!", "Okay", this).show();
            return;
        }

        // compare it 3 times
        compare().thenCompose(x -> compare()).thenCompose(x -> compare());
    }

    /**
     * @return A completable future which plays the database recording, then the users' recording so that users
     * can compare.
     */
    private CompletableFuture<Void> compare() {
        return playName().thenCompose(x -> recordingWidget.getRecording().play());
    }

    /**
     * Handle the next button being clicked by either changing the menu to the next name or exiting to the main menu
     * if there are no names left.
     */
    @FXML
    private void onNextClicked() {
        if (remainingNames.isEmpty()) {

            if (recordingWidget.isRecording()) {
                recordingWidget.stopRecording();
            }

            // use JFXDialogHelper to create and show a new pop-up dialog
            // give the user their money
            int coinsEarned = total * Constants.COINS_PER_PRACTICE;
            shop.addToBalance(coinsEarned);
            JFXDialogHelper dialog = new JFXDialogHelper("Practice Over",
                    NameSayerSettings.getInstance().getWellDoneMessage() +
                            "\n\nLipCoins\u2122 earned: " + coinsEarned, "Thanks", stackPane);
            // go back to the main menu when the user clicks the dialog's button
            dialog.setNextScene(getScene(), mainMenu);
            dialog.show();
        } else {
            // update to the next name
            current.set(remainingNames.pollFirst());
            attemptView.setNames(current.get());
        }
    }

}
