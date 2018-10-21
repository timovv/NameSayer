package namesayer.app.shop;

public class WishingWell extends RepurchaseableShopItem {

    private final String name = "The Wishing Well";
    private final int price = 100;
    private final String description = "Are you tired of having too much money?\n" +
            "Throw it down the wishing well for one wish per 100 LipCoins\u2122 (wish not guaranteed).";

    @Override
    protected void doAction() {
        // do nothing!
    }

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
}
