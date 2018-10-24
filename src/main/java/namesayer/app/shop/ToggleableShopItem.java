package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Represents a ShopItem that can be toggled between active and inactive. Once purchased, this ShopItem is permanently
 * available and never needs to be purchased again.
 */
public abstract class ToggleableShopItem implements ShopItem {

    private final BooleanProperty activeProperty = new SimpleBooleanProperty();
    private final BooleanProperty purchasedProperty = new SimpleBooleanProperty();

    protected ToggleableShopItem() {
        activeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                activate();
            } else {
                deactivate();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setActive(boolean active) {
        activeProperty.set(active);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isActive() {
        return activeProperty.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final BooleanProperty activeProperty() {
        return activeProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setPurchased(boolean purchased) {
        purchasedProperty.set(purchased);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final BooleanProperty purchasedProperty() {
        return purchasedProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isPurchased() {
        return purchasedProperty.get();
    }

    // -- abstract methods

    /**
     * Called when this ShopItem's effect is activated.
     */
    protected abstract void activate();

    /**
     * Called when this ShopItem's effect is deactivated.
     */
    protected abstract void deactivate();
}
