package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;

import java.net.URL;

/**
 * Interface representing an item in the NameSayer shop.
 */
public interface ShopItem {

    /**
     * @return The name of this ShopItem.
     */
    String getName();

    /**
     * @return A short description of what this ShopItem is, and what will happen when it is purchased/activated.
     */
    String getDescription();

    /**
     * The price of this ShopItem.
     */
    int getPrice();

    /**
     * Activate or deactivate this ShopItem. Only call this method if the item has already been purchased.
     * @param active true to mark this item as active; false otherwise.
     */
    void setActive(boolean active);

    /**
     * @return A JavaFX property representing whether this ShopItem is active or not.
     */
    BooleanProperty activeProperty();

    /**
     * @return true if this item is currently active; false otherwise.
     */
    boolean isActive();

    /**
     * Mark this item as purchased (or not)
     * @param purchased true to mark the item as purchased; false otherwise.
     */
    void setPurchased(boolean purchased);

    /**
     * @return A JavaFX property representing whether this ShopItem is purchased or not.
     */
    BooleanProperty purchasedProperty();

    /**
     * @return true if this ShopItem has been purchased; false otherwise.
     */
    boolean isPurchased();
}