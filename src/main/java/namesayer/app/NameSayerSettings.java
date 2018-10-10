package namesayer.app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class NameSayerSettings {

    private StringProperty themeProperty = new SimpleStringProperty();

    private StringProperty wellDoneMessageProperty = new SimpleStringProperty();

    private static NameSayerSettings instance;

    private static final NameSayerSettings DEFAULT_SETTINGS;

    static {
        DEFAULT_SETTINGS = new NameSayerSettings();
        DEFAULT_SETTINGS.themeProperty.setValue("main");
        DEFAULT_SETTINGS.wellDoneMessageProperty.setValue("Well done!");
    }

    private NameSayerSettings() {
    }

    public static NameSayerSettings getInstance() {
        if(instance == null) {
            instance = new NameSayerSettings();
            instance.themeProperty.setValue(DEFAULT_SETTINGS.getTheme());
            instance.wellDoneMessageProperty.setValue(DEFAULT_SETTINGS.getWellDoneMessage());
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
}

