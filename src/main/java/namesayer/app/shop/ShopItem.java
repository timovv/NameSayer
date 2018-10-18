package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;

import java.net.URL;

public interface ShopItem {

    String getName();

    String getDescription();

    int getPrice();

    void setActive(boolean active);

    BooleanProperty activeProperty();

    boolean isActive();

    void setPurchased(boolean purchased);

    BooleanProperty purchasedProperty();

    boolean isPurchased();
}