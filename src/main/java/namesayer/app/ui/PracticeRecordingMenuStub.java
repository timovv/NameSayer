package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;

/**
 * Menu where users can make practice recordings and listen to names
 */
public class PracticeRecordingMenuStub extends StackPane {

    @FXML
    private StackPane stackPane;

    @FXML
    private RecordingWidget recordingWidget;

    @FXML
    private Label countLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button nextButton;

    @FXML
    private VBox contentVBox;


    private Parent previous;
    private NameSayerDatabase database;
    private AudioSystem audioSystem;


    public PracticeRecordingMenuStub(Parent previous, AudioSystem as, NameSayerDatabase db) {
        this.previous = previous;
        this.audioSystem = as;
        this.database = db;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/practiceRecordingMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load UI", e);
        }
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }

    @FXML
    private void onPlayClicked() {
    }

    @FXML
    private void onNextClicked() {
        // use JFXDialogHelper to create and show a new pop-up dialog
        JFXDialogHelper dialog = new JFXDialogHelper("Practice Over",
                NameSayerSettings.getInstance().getWellDoneMessage(), "Thanks", stackPane);
        dialog.setNextScene(getScene(), new MainMenu(database, audioSystem));
        dialog.show();
    }
}
