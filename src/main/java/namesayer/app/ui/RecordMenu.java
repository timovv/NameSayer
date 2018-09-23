package namesayer.app.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import namesayer.app.database.NameInfo;

import java.io.IOException;

public class RecordMenu extends BorderPane {

    private Parent previous;

    private AudioClip recording = null;
    private AudioSystem audioSystem;
    private NameDatabase database;
    private Timeline autoStopTimeline;
    private Timeline countdownTimeline;
    private Timeline micLevelTimeline;
    private IntegerProperty secondsLeft = new SimpleIntegerProperty(0);

    @FXML
    private Text recordingTime;
    @FXML
    private TextField nameTextField;
    @FXML
    private ProgressBar micLevelBar;

    public RecordMenu(Parent previous, AudioSystem audio, NameDatabase db) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recordMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        this.previous = previous;
        this.audioSystem = audio;
        this.database = db;
        recordingTime.textProperty().bind(Bindings.when(secondsLeft.isEqualTo(0)).then("")
                .otherwise(secondsLeft.asString()));

        autoStopTimeline = new Timeline(new KeyFrame(Duration.millis(5000), a -> stopRecording()));
        autoStopTimeline.setCycleCount(1);
        countdownTimeline = new Timeline(new KeyFrame(Duration.millis(1000), a -> secondsLeft.set(secondsLeft.get() - 1)));
        countdownTimeline.setCycleCount(5);
    }

    @FXML
    private void initialize() {
        micLevelTimeline = new Timeline(new KeyFrame(Duration.millis(50),
                a -> micLevelBar.setProgress(1 - (audioSystem.getInputLevel() / -75.)))); // nominate -75b as our 0 reference since it seems to work
        micLevelTimeline.setCycleCount(Animation.INDEFINITE);
        micLevelTimeline.play();
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
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
        if(recording != null) {
            recording.play();
        }
    }

    @FXML
    private void saveButtonClicked() {

        if(nameTextField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a name.").showAndWait();
            return;
        }

        if(recording == null) {
            new Alert(Alert.AlertType.WARNING, "You need to make a recording before you can save. Press the record button to start!");
            return;
        }

        database.addName(nameTextField.getText(), "se206", recording);
        new Alert(Alert.AlertType.INFORMATION, "Recording created successfully!").showAndWait();
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
}
