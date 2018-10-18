package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

public class SpectrumGUIPack extends ShopItemBase {

    private final String spectrumTheme = "spectrum";
    private final String name = "Spectrum GUI Pack";
    private final int price = 1000;

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
        NameSayerSettings.getInstance().setTheme(spectrumTheme);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setTheme(NameSayerSettings.defaultSettings().getTheme());
    }
}
