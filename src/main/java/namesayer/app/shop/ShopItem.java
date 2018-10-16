package namesayer.app.shop;

import java.net.URL;

public interface ShopItem {

    String getName();

    int getPrice();

    void activate();

    void deactivate();

    boolean isActive();
}
