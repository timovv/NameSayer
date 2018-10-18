package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class IncreasedRecordingTime extends ShopItemBase {

    private final Integer longerRecordingTime = 10;

    @Override
    public String getName() {
        return "Increased Recording Time";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getPrice() {
        return 500;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setRecordingTime(longerRecordingTime);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setRecordingTime(NameSayerSettings.defaultSettings().getRecordingTime());
    }
}
