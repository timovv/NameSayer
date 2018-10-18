package namesayer.app.shop;

public class WishingWellShopItem extends OneTimePurchaseShopItem {

    @Override
    protected void doAction() {
        // do nothing!
    }

    @Override
    public String getName() {
        return "Wishing well";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getPrice() {
        return 100;
    }
}
