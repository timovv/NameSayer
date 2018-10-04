package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import namesayer.app.NameSayerException;

import java.io.IOException;

/**
 * The menu where users buy things from the LipCoins they have earned.
 */
public class ShopMenu extends StackPane {

    private Parent previous;

    public ShopMenu(Parent previous) {
        this.previous = previous;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shopMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }
    }

    @FXML
    private void onSFXClicked() {
        //testing purposes only
        System.out.println("SFX click detected.");
    }

    @FXML
    private void onSpectrumGUIClicked() {
        //testing purposes only
        System.out.println("SpectrumGUI click detected.");
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
