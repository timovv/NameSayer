package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;

import java.io.IOException;

/**
 * Menu where users can make practice recordings and listen to names
 */
public class PracticeRecordingMenuStub extends BorderPane {

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
    private NameDatabase database;
    private AudioSystem audioSystem;


    public PracticeRecordingMenuStub(Parent previous, AudioSystem as, NameDatabase db) {
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have finished all your practices!" +
                "\nGood job!");
        alert.setTitle(null);
        alert.setTitle("Well done!");
        alert.showAndWait();

        getScene().setRoot(new MainMenu(database, audioSystem));
    }
}
