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
import javafx.scene.layout.BorderPane;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PracticeRecordingMenu extends BorderPane {

    @FXML
    private RecordingWidget recordingWidget;

    @FXML
    private Label countLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button nextButton;

    private NameDatabase database;
    private AudioSystem audioSystem;
    private Parent previous;
    private int total;
    private ObjectProperty<Name> current;
    private LinkedList<Name> remainingNames;

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

        nameLabel.textProperty().bind(Bindings.createStringBinding(() -> Util.toTitleCase(current.get().getName()), current));
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> "(" + (total - remainingNames.size()) + "/" + total + ")", current));
        nextButton.textProperty().bind(Bindings.when(Bindings.createBooleanBinding(remainingNames::isEmpty, current)).then("Finish").otherwise("Next"));
    }

    @FXML
    private void initialize() {
        recordingWidget.setAudioSystem(audioSystem);
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
