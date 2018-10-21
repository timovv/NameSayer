package namesayer.app.shop;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * A shop purchase that "mines" one coin per second so that users can earn coins while they use the app.
 */
public class LipCoinMiner extends ToggleableShopItem {

    private final int lipCoinMiningRatePerSecond = 1;
    private final NameSayerShop shop;
    private final Timeline timeline;
    private final String name = "LipCoin\u2122 Miner";
    private final int price = 1000;
    private final String description = "Tired of earning LipCoins\u2122? Have this app this app earn them for you!\n" +
            "The LipCoin\u2122 miner will give you one additional LipCoin\u2122 every second!";

    public LipCoinMiner(NameSayerShop shop) {
        this.shop = shop;
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                event -> shop.addToBalance(lipCoinMiningRatePerSecond)));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
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

    /**
     * Start the coin mining timer
     */
    @Override
    public void activate() {
        timeline.playFromStart();
    }

    /**
     * Stops the coin mining timer
     */
    @Override
    public void deactivate() {
        timeline.pause();
    }
}
