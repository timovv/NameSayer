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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.Playable;
import namesayer.app.database.AttemptInfo;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Menu where users can make practice recordings and listen to names
 */
public class PracticeRecordingMenu extends StackPane {

    private final Timeline micLevelTimeline;

    @FXML
    private StackPane stackPane;

    @FXML
    private RecordingWidget recordingWidget;

    @FXML
    private Label countLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button nextButton;

    @FXML
    private VBox contentVBox;

    @FXML
    private ProgressBar micLevelBar;

    private NameSayerDatabase database;
    private final Parent mainMenu;
    private AudioSystem audioSystem;
    private Parent previous;
    private int total;
    private ObjectProperty<List<Name>> current;
    private final NameSayerShop shop;
    private LinkedList<List<Name>> remainingNames;
    private ScrollPane scrollPane = new ScrollPane();
    private AttemptView attemptView;

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
                    value = (value >= 0) ? value : 0;
                    micLevelBar.setProgress(value);
                })); // nominate -75b as our 0 reference since it seems to work
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

    @FXML
    private void initialize() {

        recordingWidget.setAudioSystem(audioSystem);

        scrollPane.setFitToWidth(true);
        attemptView = new AttemptView(current.get(), database);
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

    @FXML
    private void onBackClicked() {
        if(recordingWidget.isRecording()) {
            recordingWidget.stopRecording();
        }

        getScene().setRoot(previous);
    }

    @FXML
    private void onPlayClicked() {
        playName();
    }

    private CompletableFuture<Void> playName() {
        CompletableFuture<Void> next = CompletableFuture.completedFuture(null);
        for (Name name : current.get()) {
            next = next.thenCompose(x -> name.getRecording()).thenCompose(AudioClip::play);
        }

        return next;
    }

    @FXML
    private void onNextClicked() {
        if (remainingNames.isEmpty()) {

            if(recordingWidget.isRecording()) {
                recordingWidget.stopRecording();
            }

            // use JFXDialogHelper to create and show a new pop-up dialog
            int coinsEarned = total * NameSayerSettings.getInstance().getCoinsPerPractice();
            shop.addToBalance(coinsEarned);
            JFXDialogHelper dialog = new JFXDialogHelper("Practice Over",
                    NameSayerSettings.getInstance().getWellDoneMessage() +
                            "\n\nLipCoins\u2122 earned: " + coinsEarned, "Thanks", stackPane);
            dialog.setNextScene(getScene(), mainMenu);
            dialog.show();
        } else {
            current.set(remainingNames.pollFirst());
            attemptView.setNames(current.get());
        }
    }

}
