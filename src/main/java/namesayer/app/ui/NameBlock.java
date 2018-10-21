package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.Constants;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;
import java.net.URL;

/**
 * A generic block which represents a name, as used in {@link AbstractNameView}
 */
public class NameBlock extends StackPane {

    private final Name name;
    private final NameSayerDatabase db;

    private StackPane parent;
    private NameSayerShop shop;

    @FXML
    private Text nameText;
    @FXML
    private Text dateText;

    @FXML
    private StackPane setBadQualityButton;
    @FXML
    private StackPane unsetBadQualityButton;

    public NameBlock(Name name, NameSayerDatabase db, NameSayerShop shop, URL fxmlLocation, StackPane parent) {
        this.name = name;
        this.db = db;
        this.shop = shop;
        this.parent = parent;
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
        // Todo: Give the user some feedback when they do this
        shop.addToBalance(Constants.COINS_PER_THUMBS_DOWN);
        setBadQualityButton.setVisible(false);
        unsetBadQualityButton.setVisible(true);
    }

    @FXML
    private void unsetBadQualityClicked() {
        name.setBadQuality(false);
        shop.addToBalance(Constants.COINS_PER_THUMBS_DOWN);
        setBadQualityButton.setVisible(true);
        unsetBadQualityButton.setVisible(false);
    }

    @FXML
    private void removeButtonClicked() {
        JFXDialogLayout contents = new JFXDialogLayout();

        contents.setHeading(new Text("Are you sure you want to delete this name?"));
        JFXButton deleteButton = new JFXButton("Delete");
        JFXButton cancelButton = new JFXButton("Cancel");

        contents.setActions(cancelButton, deleteButton);
        JFXDialog dialog = new JFXDialog(this.parent, contents, JFXDialog.DialogTransition.CENTER);

        cancelButton.setOnAction(x -> dialog.close());
        deleteButton.setOnAction(x -> {
            // delete the attempt
            db.getNameDatabase().remove(name);
            dialog.close();
        });

        dialog.show();
    }

    /**
     * Inheritors should override this method if they want updates to happen when these observables change
     */
    public Observable[] getObservables() {
        return new Observable[0];
    }

}
