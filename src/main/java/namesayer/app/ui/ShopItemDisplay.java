package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.shop.ShopItem;

/**
 * Display component representing a single shop item.
 */
public class ShopItemDisplay extends StackPane {

    /**
     * The shop item that this display shows
     */
    private final ObjectProperty<ShopItem> shopItemProperty;

    /**
     * The event handler for when this is clicked
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;

    /**
     * Label showing the item's name
     */
    @FXML
    private Text shopItemLabel;

    /**
     * Label showing the item's price if it is unpurchased, or showing that the item is purchased if it is purchased
     */
    @FXML
    private Text shopItemCost;

    /**
     * The icon for this shop item
     */
    @FXML
    private ImageView shopItemIcon;

    /**
     * The image shown when this item is active
     */
    private static final Image PACKAGE_ACTIVE_IMAGE =
            new Image(ShopItemDisplay.class.getResource("/images/package-active.png").toString());

    /**
     * The image shown when this item is inactive
     */
    private static final Image PACKAGE_INACTIVE_IMAGE =
            new Image(ShopItemDisplay.class.getResource("/images/package.png").toString());

    /**
     * Create a new ShopItemDisplay representing the given ShopItem
     *
     * @param shopItem The ShopItem to show
     */
    public ShopItemDisplay(ShopItem shopItem) {
        shopItemProperty = new SimpleObjectProperty<>(shopItem);
        onAction = new SimpleObjectProperty<>();

        Util.loadFxmlComponent(getClass().getResource("/fxml/shopItem.fxml"), this);

        shopItemLabel.setText(shopItem.getName());

        shopItemCost.textProperty().bind(
                Bindings.when(shopItem.purchasedProperty()).then(Bindings.when(shopItem.activeProperty())
                        .then("Purchased, active!").otherwise("Purchased!"))
                        .otherwise(Integer.toString(shopItem.getPrice()) + " LipCoins\u2122"));

        shopItemIcon.imageProperty()
                .bind(Bindings.when(shopItem.activeProperty())
                        .then(PACKAGE_ACTIVE_IMAGE)
                        .otherwise(PACKAGE_INACTIVE_IMAGE));
    }

    /**
     * Calls the callback (if it exists) when the item is clicked
     */
    @FXML
    private void onItemClicked() {
        if (onAction.getValue() != null) {
            onAction.getValue().handle(new ActionEvent());
        }
    }

    /**
     * @return The event handler which is triggered when the item is clicked
     */
    public EventHandler<ActionEvent> getOnAction() {
        return onAction.getValue();
    }

    /**
     * @return a JavaFX property representing the OnAction event handler
     */
    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    /**
     * Set the on action event handler to the given value
     *
     * @param handler A callback which will be called when this shop item is clicked
     */
    public void setOnAction(EventHandler<ActionEvent> handler) {
        onAction.setValue(handler);
    }
}
