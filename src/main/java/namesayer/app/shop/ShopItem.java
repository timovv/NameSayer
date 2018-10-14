package namesayer.app.shop;

import java.net.URL;

public interface ShopItem {

    String getName();

    URL getIconURL();

    int getPrice();

    void activate();

    void deactivate();

    boolean isActive();
}
