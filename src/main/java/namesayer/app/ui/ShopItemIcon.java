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
import java.net.URL;

public class ShopItemIcon extends StackPane {


    private final ShopItem shopItem;
    private final String itemName;
    private final int cost;
    private final URL imagePath;

    @FXML
    private ImageView shopItemIcon;

    @FXML
    private Text shopItemName;

    @FXML
    private Text shopItemCost;

    public ShopItemIcon(ShopItem shopItem, String itemName, int cost, URL imagePath) {
        this.shopItem = shopItem;
        this.itemName = itemName;
        this.cost = cost;
        this.imagePath = imagePath;

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
        shopItemName.setText(itemName);
        shopItemCost.setText("" + cost);

        if (shopItem.isActive()) {
            shopItemIcon.setImage(new Image(getClass().getResourceAsStream("/images/package-active.png")));
        } else {
            shopItemIcon.setImage(new Image(getClass().getResourceAsStream("/images/package.png")));
        }
    }

    @FXML
    private void onItemClicked() {
        LipCoin.tryPurchase(cost);
    }
}
