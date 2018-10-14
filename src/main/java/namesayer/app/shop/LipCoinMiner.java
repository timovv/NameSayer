package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class LipCoinMiner implements ShopItem {

    private final int lipCoinMiningRate = 1;

    @Override
    public String getName() {
        return "LipCoin Miner";
    }

    @Override
    public URL getIconURL() {
        try {
            return new URL("/images/package.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPrice() {
        return 500;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setLipCoinMiner(lipCoinMiningRate);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setWellDoneMessage(NameSayerSettings.defaultSettings().getWellDoneMessage());
    }

    @Override
    public boolean isActive() {
        return NameSayerSettings.getInstance().getLipCoinMiner().equals(lipCoinMiningRate);
    }
}
