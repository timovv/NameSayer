package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;

import java.io.IOException;

public class AttemptBlock extends BorderPane {

    @FXML
    private Text attemptText;

    private final Name name;
    private final Attempt attempt;
    private final int index;

    public AttemptBlock(Name name, Attempt attempt, int index) {
        this.name = name;
        this.attempt = attempt;
        this.index = index;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nameBlockAttempt.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch(IOException e) {
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
        name.removeAttempt(attempt);
    }
}
