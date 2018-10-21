package namesayer.app.shop;

import namesayer.app.NameSayerMain;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AugmentedRealityMode extends OneTimePurchaseShopItem {
    private final String name = "Augmented Reality Mode";
    private final int price = 10000;
    private final String uri = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private final String description = "Plug in an augmented reality headset to experience this app\n" +
            "in augmented reality mode!";

    @Override
    protected void doAction() {
        NameSayerMain.hostServices().showDocument(uri);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getPrice() {
        return this.price;
    }
}
