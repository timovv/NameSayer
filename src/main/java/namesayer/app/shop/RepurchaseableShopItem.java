package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * An abstract class for shop items that can be purchased several times and have an immediate action.
 */
public abstract class RepurchaseableShopItem implements ShopItem {

    @Override
    public void setActive(boolean active) {
        if(active) {
            doAction();
        }
    }

    /**
     * Called when the item is purchased. Use this to do something.
     */
    protected abstract void doAction();

    /**
     * @return A property which will always be false.
     */
    @Override
    public BooleanProperty activeProperty() {
        // always return false
        return new SimpleBooleanProperty(false);
    }

    /**
     * @return false; this item does not have 'activity' and so cannot be active.
     */
    @Override
    public boolean isActive() {
        // this is never active
        return false;
    }

    /**
     * This method does nothing: nothing happens when the item is purchased, only when it is set active.
     */
    @Override
    public void setPurchased(boolean purchased) {
        // do nothing
    }

    /**
     * @return A property that is always false; this item must be able to be repurchased.
     */
    @Override
    public BooleanProperty purchasedProperty() {
        return new SimpleBooleanProperty(false);
    }

    /**
     * @return false; this item can never be in a 'purchased' state.
     */
    @Override
    public boolean isPurchased() {
        return false;
    }
}
