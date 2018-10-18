package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.shop.NameSayerShop;
import namesayer.app.shop.ShopItem;

import java.io.IOException;

/**
 * The menu where users buy things from the LipCoins they have earned.
 */
public class ShopMenu extends StackPane {

    private final NameSayerShop shop;
    private final Parent previous;

    @FXML
    private Pane shopPane;

    @FXML
    private Text lipCoinLabel;

    public ShopMenu(Parent previous, NameSayerShop shop) {
        this.shop = shop;
        this.previous = previous;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shopMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        for (ShopItem item : shop.getAvailableItems()) {
            ShopItemDisplay display = new ShopItemDisplay(item);
            display.setOnAction(x -> handleItemClicked(item));
            shopPane.getChildren().add(display);
        }

        lipCoinLabel.textProperty().bind(shop.balanceProperty().asString().concat(" LipCoins\u2122."));
    }

    private void handleItemClicked(ShopItem item) {
        if (shop.isPurchased(item)) {
            // activate /deactivate
            if (item.isActive()) {
                item.deactivate();
            } else {
                item.activate();
            }
        } else {
            if (shop.getBalance() < item.getPrice()) {
                new JFXDialogHelper("Insufficient funds",
                        "You do not have enough LipCoins\u2122 for this purchase.",
                        "Okay",
                        this).show();
                return;
            }

            // go ahead with purchase
            shop.purchase(item);
            item.activate();
        }
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
