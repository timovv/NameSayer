package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class OneTimePurchaseShopItem implements ShopItem {

    @Override
    public void setActive(boolean active) {
        if(active) {
            doAction();
        }
    }

    protected abstract void doAction();

    @Override
    public BooleanProperty activeProperty() {
        // always return false
        return new SimpleBooleanProperty(false);
    }

    @Override
    public boolean isActive() {
        // this is never active
        return false;
    }

    @Override
    public void setPurchased(boolean purchased) {
        // do nothing
    }

    @Override
    public BooleanProperty purchasedProperty() {
        return new SimpleBooleanProperty(false);
    }

    @Override
    public boolean isPurchased() {
        return false;
    }
}
