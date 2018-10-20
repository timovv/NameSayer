package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

public class SpectrumGUIPack extends ShopItemBase {

    private final String spectrumTheme = "spectrum";
    private final String name = "Spectrum GUI Pack";
    private final int price = 1000;
    private final String description = "Who doesn't like Rainbows? If you're bored with just two colors,\n" +
            "try a theme with all 16 million colors of the spectrum!";

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
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
