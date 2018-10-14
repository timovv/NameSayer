package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.AttemptInfo;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Menu where users can make practice recordings and listen to names
 */
public class PracticeRecordingMenu extends StackPane {

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

    private NameSayerDatabase database;
    private AudioSystem audioSystem;
    private Parent previous;
    private int total;
    private ObjectProperty<List<Name>> current;
    private LinkedList<List<Name>> remainingNames;
    private ScrollPane scrollPane = new ScrollPane();
    private AttemptView attemptView;

    public PracticeRecordingMenu(Parent previous, AudioSystem audioSystem, NameSayerDatabase db, List<List<Name>> names) {

        this.audioSystem = audioSystem;
        this.previous = previous;
        this.database = db;
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
        getScene().setRoot(previous);
    }

    @FXML
    private void onPlayClicked() {
        playName();
    }

    private CompletableFuture<Void> playName() {
        List<Name> names = current.get();
        CompletableFuture<Void> next = CompletableFuture.completedFuture(null);
        for (Name name : current.get()) {
            next = next.thenCompose(x -> name.getRecording()).thenCompose(AudioClip::play);
        }

        return next;
    }

    @FXML
    private void onNextClicked() {
        if (remainingNames.isEmpty()) {
            // use JFXDialogHelper to create and show a new pop-up dialog
            JFXDialogHelper dialog = new JFXDialogHelper("Practice Over",
                    NameSayerSettings.getInstance().getWellDoneMessage(), "Thanks", stackPane);
            dialog.setNextScene(getScene(), new MainMenu(database, audioSystem));
            dialog.show();
        } else {
            current.set(remainingNames.pollFirst());
            attemptView.setNames(current.get());
        }
    }

}
