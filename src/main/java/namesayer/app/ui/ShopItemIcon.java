package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.shop.LipCoin;
import namesayer.app.shop.ShopItem;

import java.io.IOException;

public class ShopItemIcon extends StackPane {


    private ShopItem shopItem;

    @FXML
    private ImageView shopItemIcon;

    @FXML
    private Text shopItemName;

    @FXML
    private Text shopItemCost;

    public ShopItemIcon(ShopItem shopItem) {
        this.shopItem = shopItem;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/shopItem.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load shop item", e);
        }
    }

    @FXML
    protected final void initialize() {
        shopItemName.setText(shopItem.getName());

        if (shopItem.isActive()) {
            shopItemIcon.setImage(new Image(getClass().getResourceAsStream("/images/package-active.png")));
            shopItemCost.setText("Active");
        } else {
            shopItemIcon.setImage(new Image(getClass().getResourceAsStream("/images/package.png")));
            shopItemCost.setText("" + shopItem.getPrice());
        }
    }

    @FXML
    private void onItemClicked() {
        LipCoin.tryPurchase(shopItem.getPrice());
    }
}
