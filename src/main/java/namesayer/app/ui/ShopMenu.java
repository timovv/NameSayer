package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        lipCoinLabel.textProperty().bind(shop.balanceProperty().asString().concat(" LipCoins\u2122"));
    }

    private void handleItemClicked(ShopItem item) {
        if (item.isPurchased()) {
            // activate /deactivate
            item.setActive(!item.isActive());
        } else {
            JFXDialogLayout contents = new JFXDialogLayout();

            contents.setHeading(new Text(item.getName()));
            contents.setBody(new VBox(new Text(item.getDescription()),
                    new Text("Cost: " + item.getPrice() + " LipCoins\u2122")));

            JFXButton purchaseButton = new JFXButton("Purchase");
            JFXButton cancelButton = new JFXButton("Cancel");

            if(shop.getBalance() < item.getPrice()) {
                purchaseButton.setDisable(true);
            }

            contents.setActions(cancelButton, purchaseButton);

            JFXDialog dialog = new JFXDialog(this, contents, JFXDialog.DialogTransition.CENTER);

            cancelButton.setOnAction(x -> dialog.close());

            purchaseButton.setOnAction(x -> {
                // go ahead with purchase
                shop.purchase(item);
                item.setActive(true);
                dialog.close();
            });

            dialog.show();
        }
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
