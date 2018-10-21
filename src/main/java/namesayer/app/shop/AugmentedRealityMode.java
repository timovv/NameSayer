package namesayer.app.shop;

import namesayer.app.NameSayerMain;

/**
 * A shop purchase which purportedly activates "Augmented Reality Mode", but actually just plays a funny video in
 * the users' browser.
 */
public class AugmentedRealityMode extends RepurchaseableShopItem {
    private final String name = "Augmented Reality Mode";
    private final int price = 10000;
    private final String uri = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private final String description = "Plug in an augmented reality headset to experience this app\n" +
            "in augmented reality mode!";

    /**
     * Opens the video in the users' browser.
     */
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
