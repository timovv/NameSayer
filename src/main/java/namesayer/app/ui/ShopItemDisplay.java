package namesayer.app.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

    public ShopItemDisplay(ShopItem shopItem) {
        shopItemProperty = new SimpleObjectProperty<>(shopItem);
        onAction = new SimpleObjectProperty<>();

        Util.loadFxmlComponent(getClass().getResource("/fxml/shopItem.fxml"), this);

        shopItemLabel.setText(shopItem.getName());
        shopItemCost.setText(Integer.toString(shopItem.getPrice()));
    }

    @FXML
    private void onItemClicked() {
        if(onAction.getValue() != null) {
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
