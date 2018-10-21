package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Attempt;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;

/**
 * A block which represents a single attempt. These are currently used in the
 * PracticeRecordingMenu, so you can listen to your previous attempts.
 */
public class AttemptBlock extends StackPane {

    private final Attempt attempt;
    private final NameSayerDatabase db;
    private final int index;

    private StackPane parent;

    @FXML
    private Text attemptText;

    public AttemptBlock(NameSayerDatabase db, Attempt attempt, int index, StackPane parent) {
        this.db = db;
        this.attempt = attempt;
        this.parent = parent;

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

    /**
     * Set up the text for this attempt block so that it shows the user's name
     */
    @FXML
    private void initialize() {
        attemptText.setText("Attempt " + index);
    }

    /**
     * Play the attempt when the play button is clicked.
     */
    @FXML
    private void onPlayClicked() {
        attempt.getRecording().thenApply(AudioClip::play);
    }

    /**
     * Handles when the remove button is clicked. Confirms that the user wants to delete and then deletes the attempt
     * from the database.
     */
    @FXML
    private void removeButtonClicked() {

        JFXDialogLayout contents = new JFXDialogLayout();

        contents.setHeading(new Text("Confirm Delete"));
        contents.setBody(new Text("Are you sure you want to delete this attempt?"));
        JFXButton deleteButton = new JFXButton("Delete");
        JFXButton cancelButton = new JFXButton("Cancel");

        contents.setActions(cancelButton, deleteButton);
        JFXDialog dialog = new JFXDialog(this.parent, contents, JFXDialog.DialogTransition.CENTER);

        cancelButton.setOnAction(x -> dialog.close());
        deleteButton.setOnAction(x -> {
            // delete the attempt
            db.getAttemptDatabase().remove(attempt);
            dialog.close();
        });

        dialog.show();
    }
}
