package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

public class CongratulatoryMessage extends ShopItemBase {

    private final String congratulatoryMessage = "HOLY MOLY YOU DID IT, YOU BEAST! WHAT AN ABSOLUTE MADMAN 999/10 GOOD PRACTICE LAD!!!";
    private final String name = "Congratulatory Message";
    private final int price = 300;

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
        NameSayerSettings.getInstance().setWellDoneMessage(congratulatoryMessage);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setWellDoneMessage(NameSayerSettings.defaultSettings().getWellDoneMessage());
    }
}
