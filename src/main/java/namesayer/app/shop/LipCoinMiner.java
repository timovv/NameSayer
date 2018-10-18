package namesayer.app.shop;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import namesayer.app.NameSayerSettings;

public class LipCoinMiner extends ShopItemBase {

    private final int lipCoinMiningRatePerSecond = 1;
    private final NameSayerShop shop;
    private final Timeline timeline;

    public LipCoinMiner(NameSayerShop shop) {
        this.shop = shop;
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                event -> shop.addToBalance(lipCoinMiningRatePerSecond)));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }


    @Override
    public String getName() {
        return "LipCoin Miner";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getPrice() {
        return 1000;
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
