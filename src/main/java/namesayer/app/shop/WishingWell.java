package namesayer.app.shop;

public class WishingWell extends ShopItemBase {

    private final String donationMessage = "Thank you for your donation!\nDon't forget to make a wish!";

    @Override
    public String getName() {
        return "The Wishing Well";
    }

    @Override
    public int getPrice() {
        return 100;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }
}
