package namesayer.app.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;

import java.io.IOException;

/**
 * Widget used for recording audio, playing it back and saving
 */
public class RecordingWidget extends BorderPane {

    private final int recordingTimeLimit = 10;

    private AudioClip recording = null;
    private AudioSystem audioSystem;
    private Timeline autoStopTimeline;
    private Timeline countdownTimeline;
    private IntegerProperty secondsLeft = new SimpleIntegerProperty(0);
    private Runnable saveClickedHandler = null;

    @FXML
    private Text recordingTime;

    @FXML
    private ImageView recordingButton;

    public RecordingWidget() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recordingWidget.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }
    }

    @FXML
    private void initialize() {
        // show how long they have recording
        recordingTime.textProperty().bind(Bindings.when(secondsLeft.isEqualTo(0)).then("")
                .otherwise(Bindings.concat("Recording for ", secondsLeft.asString(), "s")));

        // create the timelines, we are recording for 5s
        autoStopTimeline = new Timeline(new KeyFrame(Duration.seconds(recordingTimeLimit), a -> stopRecording()));
        autoStopTimeline.setCycleCount(1);
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), a -> secondsLeft.set(secondsLeft.get() - 1)));
        countdownTimeline.setCycleCount(recordingTimeLimit);
    }

    @FXML
    private void recordButtonClicked() {
        if (audioSystem.isRecording()) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    @FXML
    private void playButtonClicked() {
        if (getRecording() != null) {
            getRecording().play();
        }
    }

    @FXML
    private void saveButtonClicked() {
        if (saveClickedHandler != null) {
            saveClickedHandler.run();
        }
    }

    public void startRecording() {
        if(isRecording()) {
            throw new NameSayerException("Tried to start recording while recording");
        }

        audioSystem.startRecording();

        recordingButton.setImage(new Image(getClass().getResourceAsStream("/images/record-stop.png")));
        secondsLeft.set(recordingTimeLimit);
        countdownTimeline.playFromStart();
        autoStopTimeline.playFromStart();
    }

    public void stopRecording() {
        if(!isRecording()) {
            throw new NameSayerException("Tried to stop recording while not recording");
        }

        if (autoStopTimeline != null) {
            autoStopTimeline.stop();
            countdownTimeline.stop();
        }

        recordingButton.setImage(new Image(getClass().getResourceAsStream("/images/record.png")));
        secondsLeft.set(0);
        recording = audioSystem.stopRecording();
    }

    public boolean isRecording() {
        return audioSystem.isRecording();
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
