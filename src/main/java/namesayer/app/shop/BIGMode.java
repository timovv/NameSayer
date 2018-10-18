package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class BIGMode implements ShopItem {

    private final Integer scale = 2;

    @Override
    public String getName() {
        return "BIG Mode";
    }

    @Override
    public int getPrice() {
        return 500;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setGUIScale(scale);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setGUIScale(NameSayerSettings.defaultSettings().getGUIScale());
    }

    @Override
    public boolean isActive() {
        return NameSayerSettings.getInstance().getGUIScale().equals(scale);
    }
}