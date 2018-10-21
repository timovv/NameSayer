package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

/**
 * A store purchase which changes the congratulatory message when users finish their practices.
 */
public class CongratulatoryMessage extends ToggleableShopItem {

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

    /**
     * Update the well done message.
     */
    @Override
    public void activate() {
        NameSayerSettings.getInstance().setWellDoneMessage(congratulatoryMessage);
    }

    /**
     * Set the well done message back to the default.
     */
    @Override
    public void deactivate() {
        NameSayerSettings.getInstance().setWellDoneMessage(NameSayerSettings.defaultSettings().getWellDoneMessage());
    }
}
