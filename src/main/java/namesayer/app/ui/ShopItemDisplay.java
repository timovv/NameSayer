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

public class ShopItemDisplay extends StackPane {

    private final ObjectProperty<ShopItem> shopItemProperty;
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;

    @FXML
    private Text shopItemLabel;
    @FXML
    private Text shopItemCost;
    @FXML
    private ImageView shopItemIcon;

    private static final Image PACKAGE_ACTIVE_IMAGE =
            new Image(ShopItemDisplay.class.getResource("/images/package-active.png").toString());
    private static final Image PACKAGE_INACTIVE_IMAGE =
            new Image(ShopItemDisplay.class.getResource("/images/package.png").toString());

    public ShopItemDisplay(ShopItem shopItem) {
        shopItemProperty = new SimpleObjectProperty<>(shopItem);
        onAction = new SimpleObjectProperty<>();

        Util.loadFxmlComponent(getClass().getResource("/fxml/shopItem.fxml"), this);

        shopItemLabel.setText(shopItem.getName());

        shopItemCost.textProperty().bind(Bindings.when(shopItem.purchasedProperty()).then("Purchased!")
                .otherwise(Integer.toString(shopItem.getPrice())));

        shopItemIcon.imageProperty()
                .bind(Bindings.when(shopItem.activeProperty())
                        .then(PACKAGE_ACTIVE_IMAGE)
                        .otherwise(PACKAGE_INACTIVE_IMAGE));
    }

    @FXML
    private void onItemClicked() {
        if (onAction.getValue() != null) {
            onAction.getValue().handle(new ActionEvent());
        }
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction.getValue();
    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> handler) {
        onAction.setValue(handler);
    }
}
