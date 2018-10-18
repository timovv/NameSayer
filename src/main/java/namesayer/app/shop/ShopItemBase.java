package namesayer.app.shop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class ShopItemBase implements ShopItem {

    private final BooleanProperty activeProperty = new SimpleBooleanProperty();
    private final BooleanProperty purchasedProperty = new SimpleBooleanProperty();

    protected ShopItemBase() {
        activeProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                activate();
            } else {
                deactivate();
            }
        });
    }

    @Override
    public final void setActive(boolean active) {
        activeProperty.set(active);
    }

    @Override
    public final boolean isActive() {
        return activeProperty.getValue();
    }

    @Override
    public final BooleanProperty activeProperty() {
        return activeProperty;
    }

    @Override
    public final void setPurchased(boolean purchased) {
        purchasedProperty.set(purchased);
    }

    @Override
    public final BooleanProperty purchasedProperty() {
        return purchasedProperty;
    }

    @Override
    public final boolean isPurchased() {
        return purchasedProperty.get();
    }

    // -- abstract methods

    protected abstract void activate();

    protected abstract void deactivate();
}
