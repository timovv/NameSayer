package namesayer.app.shop;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class LipCoinMiner extends ShopItemBase {

    private final int lipCoinMiningRatePerSecond = 1;
    private final NameSayerShop shop;
    private final Timeline timeline;
    private final String name = "LipCoin\u2122 Miner";
    private final int price = 1000;

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
        return "";
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public void activate() {
        timeline.playFromStart();
    }

    @Override
    public void deactivate() {
        timeline.pause();
    }
}
