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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;

import java.io.IOException;

/**
 * Widget used for recording audio, playing it back and saving. This is used both in the recording menu and the
 * practice recording menu.
 */
public class RecordingWidget extends StackPane {

    /**
     * The time limit users have to make their recording. 10 seconds chosen as a reasonable maximum.
     */
    private static final int RECORDING_TIME_LIMIT = 10;

    /**
     * The recording that has been recorded by the user - null if nothing has been recorded yet
     */
    private AudioClip recording = null;

    /**
     * The AudioSystem to manage the recording
     */
    private AudioSystem audioSystem;

    /**
     * Timeline which stops recording automaticall after RECORDING_TIME_LIMIT is met.
     */
    private Timeline autoStopTimeline;

    /**
     * Timeline which updates the countdown timer as the recording progresses.
     */
    private Timeline countdownTimeline;

    /**
     * Value representing how many seconds are left during recording.
     */
    private IntegerProperty secondsLeft = new SimpleIntegerProperty(0);

    /**
     * A callback which is called when the user clicks the 'save' button to save their recording.
     */
    private Runnable saveClickedHandler = null;

    /**
     * Label showing how much time the user has left in the recording
     */
    @FXML
    private Text recordingTime;

    /**
     * Label showing whether the system is recording or not
     */
    @FXML
    private Text recordingStatus;

    /**
     * Button to click to start/stop recording
     */
    @FXML
    private ImageView recordingButton;

    /**
     * When the save button was last clicked - kept track of to prevent spamming the save button
     */
    private long lastSaveButtonClickTime = 0L;

    /**
     * Create a new Recording Widget.
     * The AudioSystem and a save button handler must be set after construction using the methods.
     */
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

    /**
     * Set up the components of the widget
     */
    @FXML
    private void initialize() {
        recordingStatus.setText("Ready to record");

        // show how long they have recording
        recordingTime.textProperty().bind(Bindings.when(secondsLeft.isEqualTo(0)).then("")
                .otherwise(Bindings.concat("Recording for ", secondsLeft.asString(), "s")));

        // create the timelines, we are recording for 5s
        autoStopTimeline = new Timeline(new KeyFrame(Duration.seconds(RECORDING_TIME_LIMIT), a -> stopRecording()));
        autoStopTimeline.setCycleCount(1);
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), a -> secondsLeft.set(secondsLeft.get() - 1)));
        countdownTimeline.setCycleCount(RECORDING_TIME_LIMIT);
    }

    /**
     * Handle the record button being clicked by either starting or stopping the recording, depending whether we are
     * recording or not.
     */
    @FXML
    private void recordButtonClicked() {
        if (audioSystem.isRecording()) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    /**
     * Handle the play button being clicked by playing the recording if present
     */
    @FXML
    private void playButtonClicked() {
        if (getRecording() != null) {
            getRecording().play();
        } else {
            JFXDialogHelper nullDialog = new JFXDialogHelper("No Recording", "You have not yet recorded anything,\n" +
                    "so there is nothing to play or save.", "I see", this);
            nullDialog.setSize(1, 1);
            nullDialog.show();

        }
    }

    /**
     * Handle the save button being clicked by calling the callback, if the recording is present
     */
    @FXML
    private void saveButtonClicked() {

        // don't let the user click save more than once per second
        // this prevents the user from accidentally spamming 'save' and saving multiple copies
        // also prevents the situation where a recording could be overwritten for having the same timestamp
        if (System.currentTimeMillis() - lastSaveButtonClickTime <= 1000) {
            return;
        }

        if (saveClickedHandler != null && getRecording() != null) {
            saveClickedHandler.run();
            lastSaveButtonClickTime = System.currentTimeMillis();
        } else {
            JFXDialogHelper nullDialog = new JFXDialogHelper("No Recording", "You have not yet recorded anything,\n" +
                    "so there is nothing to play or save.", "I see", this);
            nullDialog.setSize(1, 1);
            nullDialog.show();
        }
    }

    /**
     * Start recording the user
     *
     * @throws NameSayerException if the widget is already recording
     */
    public void startRecording() {
        if (isRecording()) {
            throw new NameSayerException("Tried to start recording while recording");
        }

        //starts the recording in the audio system
        audioSystem.startRecording();

        //change the state of the recording button to stop recording
        recordingButton.setImage(new Image(getClass().getResourceAsStream("/images/record-stop.png")));
        secondsLeft.set(RECORDING_TIME_LIMIT);
        countdownTimeline.playFromStart();
        autoStopTimeline.playFromStart();

        //disables the recording status text
        recordingStatus.setText("");
    }

    /**
     * Stop recording the user, and update the recording stored by the widget.
     *
     * @throws NameSayerException if the widget is not currently recording
     */
    public void stopRecording() {
        if (!isRecording()) {
            throw new NameSayerException("Tried to stop recording while not recording");
        }

        // stop the timelines if htey are still going
        if (autoStopTimeline != null) {
            autoStopTimeline.stop();
            countdownTimeline.stop();
        }

        // change the image and update timelines
        recordingButton.setImage(new Image(getClass().getResourceAsStream("/images/record.png")));
        secondsLeft.set(0);
        recording = audioSystem.stopRecording();
        recordingStatus.setText("Recorded");
    }

    /**
     * @return true if this widget is currently recording; false otherwise
     */
    public boolean isRecording() {
        return audioSystem.isRecording();
    }

    /**
     * Set the audio system that this RecordingWidget is using
     *
     * @param audioSystem The audio system to set to.
     */
    public void setAudioSystem(AudioSystem audioSystem) {
        this.audioSystem = audioSystem;
    }

    /**
     * Set a callback for when the user clicks the save button and a recording is present.
     *
     * @param r A Runnable which will be called.
     */
    public void setOnSaveClicked(Runnable r) {
        this.saveClickedHandler = r;
    }

    /**
     * @return The recording that has been made by the user in this RecordingWidget, or null if no recording was made.
     */
    public AudioClip getRecording() {
        return recording;
    }
}
