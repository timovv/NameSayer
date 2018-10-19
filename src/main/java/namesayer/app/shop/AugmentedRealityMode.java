package namesayer.app.shop;

public class AugmentedRealityMode extends OneTimePurchaseShopItem {
    private final String name = "Augmented Reality Mode";
    private final int price = 10000;

    @Override
    protected void doAction() {

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPrice() {
        return this.price;
    }
}
