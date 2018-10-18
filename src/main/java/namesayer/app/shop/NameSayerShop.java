package namesayer.app.shop;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import namesayer.app.NameSayerException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NameSayerShop {

    private final Set<ShopItem> allShopItems;

    private final IntegerProperty balanceProperty = new SimpleIntegerProperty();

    /**
     * Construct a new instance of the shop with default values.
     */
    public NameSayerShop() {
        // This is where shop items are added.
        allShopItems = new HashSet<>(Arrays.asList(
                new BIGMode(),
                new CongratulatoryMessage(),
                new IncreasedRecordingTime(),
                new LipCoinMiner(),
                new SpectrumGUIPack()
        ));
    }

    /**
     * @return A collection of all shop items available in the shop, purchased or not
     */
    public Set<ShopItem> getAvailableItems() {
        return Collections.unmodifiableSet(allShopItems);
    }

    /**
     * @return A set of all items purchased in the shop
     */
    public Set<ShopItem> getPurchasedItems() {
        return allShopItems.stream().filter(ShopItem::isPurchased).collect(Collectors.toSet());
    }

    /**
     * Attempt to purchase the given shop item from the shop.
     *
     * @param item The item to purchase.
     * @throws NameSayerException - if there are insufficient funds
     *                            - if the item is already purchased
     */
    public void purchase(ShopItem item) {
        if (item.isPurchased()) {
            throw new NameSayerException("Could not purchase this item since it has already been purchased!");
        }

        if (item.getPrice() > getBalance()) {
            throw new NameSayerException("Could not purchase this item: insufficient funds");
        }

        addToBalance(-item.getPrice());
        item.setPurchased(true);
    }

    /**
     * @return The current coin balance in the shop
     */
    public int getBalance() {
        return balanceProperty.getValue();
    }

    /**
     * Add the given amount to the user's coin balance.
     *
     * @param amount The amount to add to the balance.
     * @return The new balance after adding the amount.
     * @throws NameSayerException if the change would cause the balance to be negative
     */
    public int addToBalance(int amount) {
        int newBalance = balanceProperty.getValue() + amount;
        if (newBalance < 0) {
            throw new NameSayerException("That action would result in a negative balance");
        }

        balanceProperty.set(newBalance);
        return newBalance;
    }

    public ReadOnlyIntegerProperty balanceProperty() {
        return balanceProperty;
    }

    /**
     * Set the user's balance to the given value.
     *
     * @param amount The value to set the balance to.
     */
    public void setBalance(int amount) {
        balanceProperty.setValue(amount);
    }
}
