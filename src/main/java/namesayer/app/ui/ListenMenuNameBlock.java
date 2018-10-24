package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
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

/**
 * A NameBlock that is used in the listen menu, representing a single name in the database.
 */

public class ListenMenuNameBlock extends StackPane {

    /**
     * The name this block represents.
     */
    private final Name name;

    /**
     * The database the name is in.
     */
    private final NameSayerDatabase db;

    /**
     * The StackPane which is the parent of this NameBlock; used to display alerts.
     */
    private StackPane parent;

    /**
     * The shop for NameSayer. Used to give the user rewards.
     */
    private NameSayerShop shop;

    /**
     * The text of the name
     */
    @FXML
    private Text nameText;

    /**
     * The text of the date of the recording
     */
    @FXML
    private Text dateText;

    /**
     * The bad-quality rating button; clicking it marks the recording as bad quality in the database.
     */
    @FXML
    private StackPane setBadQualityButton;

    /**
     * Clicking this removes the bad quality rating from the recording.
     */
    @FXML
    private StackPane unsetBadQualityButton;

    /**
     * Construct a new ListenMenuNameBlock with the given parameters.
     *
     * @param db     The database that the name is from
     * @param shop   The shop so that shop rewards can be given to the user
     * @param name   The name this name block represents
     * @param parent The parent stack pane, used to show JFXDialogs.
     */
    public ListenMenuNameBlock(NameSayerDatabase db, NameSayerShop shop, Name name, StackPane parent) {
        this.name = name;
        this.db = db;
        this.shop = shop;
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nameBlockMain.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load name block", e);
        }

        nameText.setText(Util.toTitleCase(name.getName()));
        dateText.setText(name.getCreationDate().format(Util.DATE_TIME_FORMAT));

        // show the correct quality button - highlight the 'thumbs down' if the recording has already been marked as bad quality.
        if (name.isBadQuality()) {
            setBadQualityButton.setVisible(false);
            unsetBadQualityButton.setVisible(true);
        } else {
            setBadQualityButton.setVisible(true);
            unsetBadQualityButton.setVisible(false);
        }
    }

    /**
     * Marks the given recording as bad quality when the thumbs down button is clicked.
     */
    @FXML
    private void setBadQualityClicked() {
        name.setBadQuality(true);
        // Todo: Give the user some feedback when they do this
        shop.addToBalance(Constants.COINS_PER_THUMBS_DOWN);
        setBadQualityButton.setVisible(false);
        unsetBadQualityButton.setVisible(true);
    }

    /**
     * Removes the bad quality rating from the recording when the (highlighted) thumbs down button is clicked.
     */
    @FXML
    private void unsetBadQualityClicked() {
        name.setBadQuality(false);
        shop.addToBalance(Constants.COINS_PER_THUMBS_DOWN);
        setBadQualityButton.setVisible(true);
        unsetBadQualityButton.setVisible(false);
    }

    /**
     * Removes the recording from the database.
     *
     * @deprecated This functionality is supposed to be allowed per the NameSayer specification; the associated button has been removed.
     */
    @FXML
    @Deprecated
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
     * Plays the recorded name from the database.
     */
    @FXML
    private void onPlayClicked() {
        name.getRecording().thenAccept(AudioClip::play);
    }
}
