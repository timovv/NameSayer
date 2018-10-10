package namesayer.app.shop;

import namesayer.app.NameSayerSettings;

import java.net.URL;

public interface ShopItem {

    String getName();

    URL getIconURL();

    int getPrice();

    void activate();

    void deactivate();
}
