package namesayer.app.shop;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import namesayer.app.NameSayerException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NameSayerShop {

    private final Set<ShopItem> allShopItems;

    private final IntegerProperty balanceProperty = new SimpleIntegerProperty();

    /**
     * Construct a new instance of the shop with default values.
     */
    public NameSayerShop() {
        // This is where shop items are added.
        allShopItems = new TreeSet<>(Comparator.comparing(ShopItem::getName, String.CASE_INSENSITIVE_ORDER));
        allShopItems.addAll(Arrays.asList(
                new CongratulatoryMessage(),
                new LipCoinMiner(this),
                new SpectrumGUIPack(),
                new WishingWell(),
                new AugmentedRealityMode(),
                new BiggerAnimations()
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
     * Set the user's balance to the given value.
     *
     * @param amount The value to set the balance to.
     */
    public void setBalance(int amount) {
        balanceProperty.setValue(amount);
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

    // saving and loading

    /**
     * Save the users' purchases, balance, and active shop items to the given path.
     *
     * @param path The path to save to.
     */
    public void saveTo(Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(os)) {
                // write the balance
                outputStream.writeInt(getBalance());
                // count of purchased items
                Set<ShopItem> purchased = getPurchasedItems();
                outputStream.writeInt(purchased.size());

                for (ShopItem item : purchased) {
                    outputStream.writeUTF(item.getName());
                    // write whether it is active or not.
                    outputStream.writeBoolean(item.isActive());
                }
            }
        }
    }

    public void load(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            try (ObjectInputStream inputStream = new ObjectInputStream(is)) {
                setBalance(inputStream.readInt());
                int purchasedCount = inputStream.readInt();

                for (int i = 0; i < purchasedCount; ++i) {
                    String name = inputStream.readUTF();
                    boolean active = inputStream.readBoolean();
                    ShopItem item = allShopItems.stream().filter(x -> x.getName().equals(name))
                            .findAny().orElseThrow(() -> new IOException("Could not find shop item matching " + name));
                    item.setPurchased(true);
                    item.setActive(active);
                }
            }
        }
    }
}
