package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Menu where users can make practice recordings and listen to names
 */
public class PracticeRecordingMenu extends BorderPane {

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

    private NameDatabase database;
    private AudioSystem audioSystem;
    private Parent previous;
    private int total;
    private ObjectProperty<Name> current;
    private LinkedList<Name> remainingNames;
    private AttemptView attemptView;
    private ScrollPane scrollPane = new ScrollPane();

    public PracticeRecordingMenu(Parent previous, AudioSystem audioSystem, NameDatabase db, List<Name> names) {

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
        } catch(IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }

        // change these labels when the current name changes
        nameLabel.textProperty().bind(Bindings.createStringBinding(() -> Util.toTitleCase(current.get().getName()), current));

        // show the number of recordings they have left
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> "(" + (total - remainingNames.size()) + "/" + total + ")", current));

        // change the button if it's the last recording
        nextButton.textProperty().bind(Bindings.when(Bindings.createBooleanBinding(remainingNames::isEmpty, current)).then("Finish").otherwise("Next"));
    }

    @FXML
    private void initialize() {

        recordingWidget.setAudioSystem(audioSystem);
        attemptView = new AttemptView(current.get());

        scrollPane.setContent(attemptView);
        scrollPane.setFitToWidth(true);
        contentVBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // if the name changes, also update the attmpts
        current.addListener((observable, oldValue, newValue) -> attemptView.setName(newValue));
        // when the recording is saved, create a new attempt
        recordingWidget.setOnSaveClicked(() -> current.get().addAttempt(recordingWidget.getRecording(), LocalDateTime.now()));
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }

    @FXML
    private void onPlayClicked() {
        current.get().getRecording().thenApply(AudioClip::play);
    }

    @FXML
    private void onNextClicked() {
        if(remainingNames.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Well done!").showAndWait();
            getScene().setRoot(previous);
        } else {
            current.set(remainingNames.pollFirst());
        }
    }

}
