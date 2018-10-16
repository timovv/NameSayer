package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class IncreasedRecordingTime implements ShopItem {

    private final Integer longerRecordingTime = 10;

    @Override
    public String getName() {
        return "Increased Recording Time";
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

    @Override
    public boolean isActive() {
        return NameSayerSettings.getInstance().getRecordingTime().equals(longerRecordingTime);
    }
}
