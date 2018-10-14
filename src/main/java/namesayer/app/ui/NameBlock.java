package namesayer.app.ui;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;
import java.net.URL;

/**
 * A generic block which represents a name, as used in {@link AbstractNameView}
 */
public class NameBlock extends BorderPane {

    private final Name name;
    private final NameSayerDatabase database;

    @FXML
    private Text nameText;
    @FXML
    private Text dateText;

    @FXML
    private StackPane setBadQualityButton;
    @FXML
    private StackPane unsetBadQualityButton;

    public NameBlock(Name name, NameSayerDatabase db, URL fxmlLocation) {
        this.name = name;
        this.database = db;
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load name block", e);
        }
    }

    /**
     * Children of this class should place initialize stuff in the constructor (it will work)
     */
    @FXML
    protected final void initialize() {
        nameText.setText(Util.toTitleCase(getName().getName()));
        dateText.setText(getName().getCreationDate().format(Util.DATE_TIME_FORMAT));

        if (name.isBadQuality()) {
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
        // double check they actually want to DELET
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this recording?");
        alert.showAndWait().filter(x -> x == ButtonType.OK).ifPresent(x -> database.getNameDatabase().remove(name));
    }

    /**
     * Inheritors should override this method if they want updates to happen when these observables change
     */
    public Observable[] getObservables() {
        return new Observable[0];
    }

}
