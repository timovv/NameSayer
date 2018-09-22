package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;

import java.io.IOException;

public class MainMenu extends BorderPane {

    public MainMenu() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException ioex) {
            throw new NameSayerException("Could not create main menu", ioex);
        }
    }

    @FXML
    private void onPracticeClicked() {
        PracticeMenu menu = new PracticeMenu(this);
        getScene().setRoot(menu);
    }

    @FXML
    private void onListenClicked() {
        ListenMenu menu = new ListenMenu(this);
        getScene().setRoot(menu);
    }

    @FXML
    private void onRecordClicked() {
        RecordMenu menu = new RecordMenu(this);
        getScene().setRoot(menu);
    }
}
