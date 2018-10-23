package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    /**
     * The internal shop object used to manage purchases.
     */
    private final NameSayerShop shop;

    /**
     * The menu to go to when the back button is pressed
     */
    private final Parent previous;

    /**
     * The pane which stores the shop items
     */
    @FXML
    private Pane shopPane;

    /**
     * Label showing the users' current balance
     */
    @FXML
    private Text lipCoinLabel;

    /**
     * Create a new ShopMenu with the given parameters
     * @param previous The menu to go to when the back button is pressed
     * @param shop The shop menu.
     */
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

        // add the shop items to the shop menu, and set up the callback so that we can handle when they are clicked
        for (ShopItem item : shop.getAvailableItems()) {
            ShopItemDisplay display = new ShopItemDisplay(item);
            display.setOnAction(x -> handleItemClicked(item));
            shopPane.getChildren().add(display);
        }

        lipCoinLabel.textProperty().bind(shop.balanceProperty().asString().concat(" LipCoins\u2122"));
    }

    /**
     * Handles a shop item being clicked.
     *
     * if hte item is already purchased, toggles the activity of the item.
     * if the item is not purchased, opens a JFXDialog which allows the user to decide whether they want to
     * purchase the item or not.
     * @param item The Shopitem that was clicked
     */
    private void handleItemClicked(ShopItem item) {
        if (item.isPurchased()) {
            // activate /deactivate
            item.setActive(!item.isActive());
        } else {
            JFXDialogLayout contents = new JFXDialogLayout();

            contents.setHeading(new Text(item.getName()));
            contents.setBody(new VBox(new Text(item.getDescription()),
                    new Text("\nCost: " + item.getPrice() + " LipCoins\u2122")));

            JFXButton purchaseButton = new JFXButton("Purchase");
            JFXButton cancelButton = new JFXButton("Cancel");

            if (shop.getBalance() < item.getPrice()) {
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

    /**
     * Go back to the previous menu
     */
    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
