package namesayer.app.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;

import java.io.IOException;

public class RecordingWidget extends BorderPane {

    private AudioClip recording = null;
    private AudioSystem audioSystem;
    private Timeline autoStopTimeline;
    private Timeline countdownTimeline;
    private IntegerProperty secondsLeft = new SimpleIntegerProperty(0);
    private Runnable saveClickedHandler = null;

    @FXML
    private Text recordingTime;

    public RecordingWidget() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recordingWidget.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }
    }

    @FXML
    private void initialize() {
        recordingTime.textProperty().bind(Bindings.when(secondsLeft.isEqualTo(0)).then("")
                .otherwise(Bindings.concat("Recording for ", secondsLeft.asString(), "s")));

        autoStopTimeline = new Timeline(new KeyFrame(Duration.millis(5000), a -> stopRecording()));
        autoStopTimeline.setCycleCount(1);
        countdownTimeline = new Timeline(new KeyFrame(Duration.millis(1000), a -> secondsLeft.set(secondsLeft.get() - 1)));
        countdownTimeline.setCycleCount(5);
    }

    @FXML
    private void recordButtonClicked() {
        if(audioSystem.isRecording()) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    @FXML
    private void playButtonClicked() {
        if(getRecording() != null) {
            getRecording().play();
        }
    }

    @FXML
    private void saveButtonClicked() {
        if(saveClickedHandler != null) {
            saveClickedHandler.run();
        }
    }

    private void startRecording() {
        audioSystem.startRecording();

        secondsLeft.set(5);
        countdownTimeline.playFromStart();
        autoStopTimeline.playFromStart();
    }

    private void stopRecording() {
        if(autoStopTimeline != null) {
            autoStopTimeline.stop();
            countdownTimeline.stop();
        }

        secondsLeft.set(0);
        recording = audioSystem.stopRecording();
    }

    public void setAudioSystem(AudioSystem audioSystem) {
        this.audioSystem = audioSystem;
    }

    public void setOnSaveClicked(Runnable r) {
        this.saveClickedHandler = r;
    }

    public AudioClip getRecording() {
        return recording;
    }
}
