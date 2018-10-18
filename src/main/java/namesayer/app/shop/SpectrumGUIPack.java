package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class SpectrumGUIPack extends ShopItemBase {

    private final String spectrumTheme = "spectrum";

    @Override
    public String getName() {
        return "Spectrum GUI Pack";
    }

    @Override
    public int getPrice() {
        return 1000;
    }

    @Override
    public void activate() {
        NameSayerSettings.getInstance().setTheme(spectrumTheme);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setTheme(NameSayerSettings.defaultSettings().getTheme());
    }
}
