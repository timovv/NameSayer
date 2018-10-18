package namesayer.app.shop;

public class WishingWell extends OneTimePurchaseShopItem {

    private final String name = "The Wishing Well";
    private final int price = 100;

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
        return "";
    }

    @Override
    public int getPrice() {
        return this.price;
    }
}
