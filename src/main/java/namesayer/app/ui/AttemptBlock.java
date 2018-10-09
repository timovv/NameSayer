package namesayer.app.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Attempt;
import namesayer.app.database.NameSayerDatabase;

/**
 * A block which represents a single attempt. These are currently used in the
 * PracticeRecordingMenu, so you can listen to your previous attempts.
 */
public class AttemptBlock extends BorderPane {

    @FXML
    private Text attemptText;

    private final Attempt attempt;
    private final NameSayerDatabase db;
    private final int index;

    public AttemptBlock(NameSayerDatabase db, Attempt attempt, int index) {
        this.db = db;
        this.attempt = attempt;
        // index is just a number so we can go Attempt 1, Attempt 2, Attempt 3, etc.
        this.index = index;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nameBlockAttempt.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }
    }

    @FXML
    private void initialize() {
        attemptText.setText("Attempt " + index);
    }

    @FXML
    private void onPlayClicked() {
        attempt.getRecording().thenApply(AudioClip::play);
    }

    @FXML
    private void removeButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this attempt?");
        alert.showAndWait().filter(x -> x == ButtonType.OK).ifPresent(x -> {
            db.getAttemptDatabase().remove(attempt);
        });
    }
}
