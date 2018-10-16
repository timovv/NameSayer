package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class CongratulatoryMessage implements ShopItem {

    private final String congratulatoryMessage = "HOLY MOLY YOU DID IT, YOU BEAST! WHAT AN ABSOLUTE MADMAN 999/10 GOOD PRACTICE LAD!!!";

    @Override
    public String getName() {
        return "Congratulatory Message";
    }

    @Override
    public int getPrice() {
        return 300;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setWellDoneMessage(congratulatoryMessage);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setWellDoneMessage(NameSayerSettings.defaultSettings().getWellDoneMessage());
    }

    @Override
    public boolean isActive() {
        return NameSayerSettings.getInstance().getWellDoneMessage().equals(congratulatoryMessage);
    }
}
