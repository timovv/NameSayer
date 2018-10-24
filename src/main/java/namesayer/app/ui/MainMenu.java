package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import namesayer.app.NameSayerException;
import namesayer.app.NameSayerSettings;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;

/**
 * The main menu - the menu which the user first sees when they open the application.
 */
public class MainMenu extends StackPane {

    /**
     * The database the application uses
     */
    private final NameSayerDatabase database;

    /**
     * The audio system used by NameSayer
     */
    private final AudioSystem audioSystem;

    /**
     * The listen menu used by NameSayer.
     */
    private final ListenMenu listenMenu;

    /**
     * The practice menu.
     */
    private final PracticeMenu practiceMenu;

    /**
     * The shop menu.
     */
    private final ShopMenu shopMenu;

    /**
     * The record menu
     */
    private final RecordMenu recordMenu;

    /**
     * This menu's scene.
     */
    private Scene scene;

    /**
     * The image for the line along the top of the menu, the image shown is dependent on the theme.
     */
    @FXML
    private ImageView spectrumBanner;

    /**
     * The image for the large banner in the middle of the menu; the image shown is dependent on the theme.
     */
    @FXML
    private ImageView soundwaveBanner;

    /**
     * Extra label saying 'spectrum' when the Spectrum GUI pack is active.
     */
    @FXML
    private Text spectrumLabel;

    /**
     * Settings object.
     */
    private NameSayerSettings settings;

    /**
     * The help window.
     */
    private Stage helpWindowStage = null;

    /**
     * Construct a new main menu with the given entities
     *
     * @param database    The database used in this instance of NameSayer.
     * @param audioSystem The audio system used by NameSayer.
     * @param shop        The NameSayer shop.
     */
    public MainMenu(NameSayerDatabase database, AudioSystem audioSystem, NameSayerShop shop) {
        // loading fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ioex) {
            throw new NameSayerException("Could not create main menu", ioex);
        }

        // setup all the menus and pass parameters
        this.database = database;
        this.audioSystem = audioSystem;
        this.listenMenu = new ListenMenu(this, database, shop);
        this.practiceMenu = new PracticeMenu(this, audioSystem, database, shop);
        this.shopMenu = new ShopMenu(this, shop);
        this.recordMenu = new RecordMenu(this, audioSystem, database, shop);

        // when the theme is changed (usually by the shop) update the theme!
        NameSayerSettings.getInstance().themeProperty().addListener((observable, oldValue, newValue) -> setTheme(newValue));
    }

    /**
     * Performs initial setup for the help menu
     */
    private void setupHelpMenu() {
        StackPane layout = new StackPane();
        WebView webView = new WebView();
        webView.getEngine().load(getClass().getResource("/help/help.html").toExternalForm());
        webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/help/help_user.css").toExternalForm());
        layout.getChildren().add(webView);

        Scene scene = new Scene(layout, 630, 800);
        helpWindowStage = new Stage();
        helpWindowStage.setResizable(false);
        helpWindowStage.setTitle("NameSayer Help");
        helpWindowStage.setScene(scene);
    }

    /**
     * Opens the practice menu when the practice button is clicked.
     */
    @FXML
    private void onPracticeClicked() {
        practiceMenu.reset();
        getScene().setRoot(practiceMenu);
    }

    /**
     * Opens the listen menu when the listen button is clicked
     */
    @FXML
    private void onListenClicked() {
        getScene().setRoot(listenMenu);
    }

    /**
     * Opens the record menu when the record button is clicked
     */
    @FXML
    private void onRecordClicked() {
        getScene().setRoot(recordMenu);
    }

    /**
     * Set the scene used when the theme changes.
     *
     * @param scene The scene to change when the theme is updated.
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Opens the shop menu when the shop button is clicked
     */
    @FXML
    private void onShopButtonClicked() {
        getScene().setRoot(shopMenu);
    }

    /**
     * Show the help menu (if it is not open already) when the help button is clicked.
     */
    @FXML
    private void onHelpButtonClicked() {
        if (helpWindowStage == null) {
            setupHelpMenu();
        }

        if (!helpWindowStage.isShowing()) {
            helpWindowStage.show();
        }
    }

    /**
     * Updates the theme to the given theme
     *
     * @param address The name of the theme to set to. Currently supported values are "spectrum" and "main".
     */
    private void setTheme(String address) {
        if (address == null) {
            return;
        }

        // update stylesheets and change the main menu images.
        if (address.equals("spectrum")) {
            scene.getStylesheets().clear();
            spectrumLabel.setVisible(true);
            spectrumBanner.setImage(new Image(getClass().getResourceAsStream("/images/banner.png")));
            soundwaveBanner.setImage(new Image(getClass().getResourceAsStream("/images/soundwave.png")));
        } else if (address.equals("main")) {
            scene.getStylesheets().clear();
            spectrumLabel.setVisible(false);
            spectrumBanner.setImage(new Image(getClass().getResourceAsStream("/images/main-banner.png")));
            soundwaveBanner.setImage(new Image(getClass().getResourceAsStream("/images/main-soundwave.png")));
        }

        scene.getStylesheets().add("/css/" + address + ".css");
    }

}
