package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;

/**
 * The main menu
 */
public class MainMenu extends StackPane {

    private final NameSayerDatabase database;
    private final AudioSystem audioSystem;
    private final ListenMenu listenMenu;
    private final PracticeMenu practiceMenu;

    public MainMenu(NameSayerDatabase database, AudioSystem audioSystem) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ioex) {
            throw new NameSayerException("Could not create main menu", ioex);
        }

        this.database = database;
        this.audioSystem = audioSystem;
        this.listenMenu = new ListenMenu(this, database);
        this.practiceMenu = new PracticeMenu(this, audioSystem, database);
    }

    @FXML
    private void onPracticeClicked() {
        getScene().setRoot(practiceMenu);
    }

    @FXML
    private void onListenClicked() {
        getScene().setRoot(listenMenu);
    }

    @FXML
    private void onRecordClicked() {
        RecordMenu menu = new RecordMenu(this, audioSystem, database);
        getScene().setRoot(menu);
    }
}
