package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.URL;

public class SpectrumGUIPack implements ShopItem {
    @Override
    public String getName() {
        return "Spectrum GUI pack";
    }

    @Override
    public URL getIconURL() {
        return null;
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setTheme("spectrum");
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setTheme(NameSayerSettings.defaultSettings().getTheme());
    }
}
