package namesayer.app.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;

import java.io.IOException;

/**
 * The menu where users create their own recordings
 */
public class RecordMenu extends BorderPane {

    private Parent previous;


    private Timeline micLevelTimeline;
    private AudioSystem audioSystem;
    private NameDatabase database;

    @FXML
    private TextField nameTextField;
    @FXML
    private ProgressBar micLevelBar;
    @FXML
    private RecordingWidget recordingWidget;

    public RecordMenu(Parent previous, AudioSystem audio, NameDatabase db) {
        this.previous = previous;
        this.audioSystem = audio;
        this.database = db;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recordMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }
    }

    @FXML
    private void initialize() {
        // set up the mic level bar
        micLevelTimeline = new Timeline(new KeyFrame(Duration.millis(50),
                a -> micLevelBar.setProgress(1 - (audioSystem.getInputLevel() / -200)))); // nominate -75b as our 0 reference since it seems to work
        micLevelTimeline.setCycleCount(Animation.INDEFINITE);
        micLevelTimeline.play();

        recordingWidget.setAudioSystem(audioSystem);
        recordingWidget.setOnSaveClicked(this::saveButtonClicked);
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }

    private void saveButtonClicked() {
        // ensure there's a name to save and a recording as well
        if(nameTextField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a name.").showAndWait();
            return;
        }

        if(recordingWidget.getRecording() == null) {
            new Alert(Alert.AlertType.WARNING, "You need to make a recording before you can save. Press the record button to start!");
            return;
        }

        database.addName(nameTextField.getText(), "se206", recordingWidget.getRecording());
        new Alert(Alert.AlertType.INFORMATION, "Recording created successfully!").showAndWait();
    }


}
