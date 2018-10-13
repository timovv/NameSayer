package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameSayerDatabase;

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

    @FXML
    ImageView spectrumBanner;

    @FXML
    ImageView soundwaveBanner;

    @FXML
    Text spectrumLabel;
    private NameSayerSettings settings;

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
        this.shopMenu = new ShopMenu(this);

        NameSayerSettings.getInstance().themeProperty().addListener((observable, oldValue, newValue) -> setTheme(newValue));
    }

    @FXML
    private void initialize() {
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

    @FXML
    private void onShopButtonClicked() {
        getScene().setRoot(shopMenu);
    }

    // change to private once the settings thing actually works
    public void setTheme(String address) {
        this.getScene().getStylesheets().clear();
        this.getScene().getStylesheets().add("/css/" + address + ".css");
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
