package namesayer.app.ui;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class NameBlock extends BorderPane {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final Name name;
    private final NameDatabase database;

    @FXML
    private Text nameText;
    @FXML
    private Text dateText;

    @FXML
    private StackPane setBadQualityButton;
    @FXML
    private StackPane unsetBadQualityButton;

    public NameBlock(Name name, NameDatabase db, URL fxmlLocation) {
        this.name = name;
        this.database = db;
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }
    }

    /**
     * Children should place initialize stuff in the constructor (it will work)
     */
    @FXML
    protected final void initialize() {
        nameText.setText(toTitleCase(getName().getName()));
        dateText.setText(getName().getCreationDate().format(FORMAT));

        if(name.isBadQuality()) {
            setBadQualityButton.setVisible(false);
            unsetBadQualityButton.setVisible(true);
        } else {
            setBadQualityButton.setVisible(true);
            unsetBadQualityButton.setVisible(false);
        }
    }

    public final Name getName() {
        return name;
    }

    private String toTitleCase(String s) {
        char[] array = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean lastWhitespace = true;
        for (char c : array) {
            if (lastWhitespace) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(Character.toLowerCase(c));
            }

            lastWhitespace = Character.isWhitespace(c);
        }

        return sb.toString();
    }

    @FXML
    private void onPlayClicked() {
        getName().getRecording().thenAccept(AudioClip::play);
    }

    @FXML
    private void setBadQualityClicked() {
        name.setBadQuality(true);
        setBadQualityButton.setVisible(false);
        unsetBadQualityButton.setVisible(true);
    }

    @FXML
    private void unsetBadQualityClicked() {
        name.setBadQuality(false);
        setBadQualityButton.setVisible(true);
        unsetBadQualityButton.setVisible(false);
    }

    @FXML
    private void removeButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this recording?");
        alert.showAndWait().filter(x -> x == ButtonType.OK).ifPresent(x -> database.removeName(name));
    }

    public Observable[] getObservables() {
        return new Observable[0];
    }

}
