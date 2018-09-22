package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import namesayer.app.NameSayerException;

import java.io.IOException;

public class PracticeMenu extends BorderPane {

    private Parent previous;

    public PracticeMenu(Parent previous) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/practiceMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        this.previous = previous;
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
