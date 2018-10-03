package namesayer.app.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;

import java.io.IOException;

/**
 * The menu where users create their own recordings
 */
public class RecordMenu extends StackPane {

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
                a -> {
                    double value = (1 - (audioSystem.getInputLevel() / -100));
                    value = (value >= 0) ? value : 0;
                    micLevelBar.setProgress(value);
                })); // nominate -75b as our 0 reference since it seems to work
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
        if (nameTextField.getText().isEmpty()) {
            // use JFXDialogHelper to create and show a new pop-up dialog for an empty name
            JFXDialogHelper dialog = new JFXDialogHelper("Empty Name", "Please enter a name to record.",
                    "OK", this);
            dialog.show();
            return;
        }

        if (recordingWidget.getRecording() == null) {
            // use JFXDialogHelper to create and show a new pop-up dialog for no recording and save
            JFXDialogHelper dialog = new JFXDialogHelper("No Recording", "Please record something before" +
                    " you try to save.", "OK", this);
            dialog.show();
            return;
        }

        database.addName(nameTextField.getText(), "se206", recordingWidget.getRecording());
        // use JFXDialogHelper to create and show a new pop-up dialog for successful recording
        JFXDialogHelper dialog = new JFXDialogHelper("Recording Successful", "Your recording of " +
                nameTextField.getText() + " was saved.", "OK", this);
        dialog.show();
    }


}
