package namesayer.app.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import namesayer.app.shop.ShopItem;

public class ShopItemDisplay extends StackPane {

    private ObjectProperty<ShopItem> shopItemProperty;

    public ShopItemDisplay(ShopItem shopItem) {
        shopItemProperty = new SimpleObjectProperty<>(shopItem);

        Util.loadFxmlComponent(getClass().getResource("/fxml/shopItem.fxml"), this);
    }

    @FXML
    private void onItemClicked() {
        Button b;
    }
}
