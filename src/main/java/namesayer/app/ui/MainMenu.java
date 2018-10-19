package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;

/**
 * The main menu, including all theme changes, etc.
 */
public class MainMenu extends StackPane {

    private final NameSayerDatabase database;
    private final AudioSystem audioSystem;
    private final ListenMenu listenMenu;
    private final PracticeMenu practiceMenu;
    private final ShopMenu shopMenu;
    private final RecordMenu recordMenu;
    private Scene scene;

    @FXML
    private ImageView spectrumBanner;

    @FXML
    private ImageView soundwaveBanner;

    @FXML
    private Text spectrumLabel;

    private NameSayerSettings settings;

    public MainMenu(NameSayerDatabase database, AudioSystem audioSystem, NameSayerShop shop) {
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
        this.listenMenu = new ListenMenu(this, database, shop);
        this.practiceMenu = new PracticeMenu(this, audioSystem, database, shop);
        this.shopMenu = new ShopMenu(this, shop);
        this.recordMenu = new RecordMenu(this, audioSystem, database, shop);

        NameSayerSettings.getInstance().themeProperty().addListener((observable, oldValue, newValue) -> setTheme(newValue));
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
        getScene().setRoot(recordMenu);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    private void onShopButtonClicked() {
        getScene().setRoot(shopMenu);
    }

    // TODO: change to private once the settings thing actually works
    public void setTheme(String address) {
        if (address == null) {
            return;
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add("/css/" + address + ".css");
        if (address.equals("spectrum")) {
            spectrumLabel.setVisible(true);
            spectrumBanner.setImage(new Image(getClass().getResourceAsStream("/images/banner.png")));
            soundwaveBanner.setImage(new Image(getClass().getResourceAsStream("/images/soundwave.png")));
        } else if (address.equals("main")) {
            spectrumLabel.setVisible(false);
            spectrumBanner.setImage(new Image(getClass().getResourceAsStream("/images/main-banner.png")));
            soundwaveBanner.setImage(new Image(getClass().getResourceAsStream("/images/main-soundwave.png")));
        }
    }
}
