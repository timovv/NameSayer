package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

public class CongratulatoryMessage extends ShopItemBase {

    private final String congratulatoryMessage = "HOLY MOLY YOU DID IT, YOU BEAST! WHAT AN ABSOLUTE MADMAN 999/10 GOOD PRACTICE LAD!!!";
    private final String name = "Congratulatory Message";
    private final int price = 300;
    private final String description = "Feeling down? Get a morale-boosting, encouraging message every time\n" +
            "you finish a practice round!";

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
        NameSayerSettings.getInstance().setWellDoneMessage(congratulatoryMessage);
    }

    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setWellDoneMessage(NameSayerSettings.defaultSettings().getWellDoneMessage());
    }
}
