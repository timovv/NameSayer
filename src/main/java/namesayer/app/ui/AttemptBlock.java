package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;

public class AttemptBlock extends BorderPane {

    @FXML
    private TextField attemptText;

    private final Name name;
    private final Attempt attempt;
    private final int index;

    public AttemptBlock(Name name, Attempt attempt, int index) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nameBlockAttempt.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.name = name;
        this.attempt = attempt;
        this.index = index;
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
        name.removeAttempt(attempt);
    }
}
