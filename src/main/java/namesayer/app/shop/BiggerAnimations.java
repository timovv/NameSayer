package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

public class BiggerAnimations extends ShopItemBase {

    private final String bigAnimations = "animations";
    private final String name = "Bigger Animations";
    private final int price = 500;
    private final String description = "Bigger animation scaling on main and recording buttons on hover.";

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
        NameSayerSettings.getInstance().setTheme(bigAnimations);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setTheme(NameSayerSettings.defaultSettings().getTheme());
    }
}
