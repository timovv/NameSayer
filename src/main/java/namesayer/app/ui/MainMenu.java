package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;

import java.io.IOException;

public class MainMenu extends BorderPane {

    private final NameDatabase database;
    private final AudioSystem audioSystem;
    private final ListenMenu listenMenu;

    public MainMenu(NameDatabase database, AudioSystem audioSystem) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException ioex) {
            throw new NameSayerException("Could not create main menu", ioex);
        }

        this.database = database;
        this.audioSystem = audioSystem;
        this.listenMenu = new ListenMenu(this, database);
    }

    @FXML
    private void onPracticeClicked() {
        PracticeMenu menu = new PracticeMenu(this);
        getScene().setRoot(menu);
    }

    @FXML
    private void onListenClicked() {
        getScene().setRoot(listenMenu);
    }

    @FXML
    private void onRecordClicked() {
        RecordMenu menu = new RecordMenu(this);
        getScene().setRoot(menu);
    }
}
