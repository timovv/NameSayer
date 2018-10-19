package namesayer.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class NameSayerSettings {

    private static final NameSayerSettings DEFAULT_SETTINGS;
    private static NameSayerSettings instance;

    static {
        DEFAULT_SETTINGS = new NameSayerSettings();
        DEFAULT_SETTINGS.themeProperty.setValue("main");
        DEFAULT_SETTINGS.wellDoneMessageProperty.setValue("You have finished all your practices.");
        DEFAULT_SETTINGS.lipCoinMinerProperty.setValue(0);
        DEFAULT_SETTINGS.coinsPerPracticeProperty.set(50);
    }

    private StringProperty themeProperty = new SimpleStringProperty();
    private StringProperty wellDoneMessageProperty = new SimpleStringProperty();
    private IntegerProperty lipCoinMinerProperty = new SimpleIntegerProperty();
    private IntegerProperty coinsPerPracticeProperty = new SimpleIntegerProperty();

    private NameSayerSettings() {
    }

    public static NameSayerSettings getInstance() {
        if (instance == null) {
            instance = new NameSayerSettings();
            instance.themeProperty.setValue(DEFAULT_SETTINGS.getTheme());
            instance.wellDoneMessageProperty.setValue(DEFAULT_SETTINGS.getWellDoneMessage());
            instance.coinsPerPracticeProperty.set(DEFAULT_SETTINGS.getCoinsPerPractice());
        }

        return instance;
    }

    public static NameSayerSettings defaultSettings() {
        return DEFAULT_SETTINGS;
    }

    public String getTheme() {
        return themeProperty.get();
    }

    public void setTheme(String theme) {
        themeProperty.set(theme);
    }

    public StringProperty themeProperty() {
        return themeProperty;
    }

    public String getWellDoneMessage() {
        return wellDoneMessageProperty.get();
    }

    public void setWellDoneMessage(String message) {
        wellDoneMessageProperty.set(message);
    }

    public StringProperty wellDoneMessageProperty() {
        return wellDoneMessageProperty;
    }

    public Integer getLipCoinMiner() {
        return lipCoinMinerProperty.get();
    }

    public void setLipCoinMiner(int lipCoinMiningRate) {
        lipCoinMinerProperty.set(lipCoinMiningRate);
    }

    public IntegerProperty lipCoinMinerProperty() {
        return lipCoinMinerProperty;
    }

    public int getCoinsPerPractice() {
        return coinsPerPracticeProperty.get();
    }

    public void setCoinsPerPractice(int value) {
        coinsPerPracticeProperty.set(value);
    }

    public IntegerProperty coinsPerPracticeProperty() {
        return coinsPerPracticeProperty;
    }

}

