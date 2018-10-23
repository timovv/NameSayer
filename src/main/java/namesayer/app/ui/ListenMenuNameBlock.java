package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import namesayer.app.Constants;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;

/**
 * A NameBlock that is used in the listen menu.
 */

public class ListenMenuNameBlock {

    private final Name name;
    private final NameSayerDatabase db;
    @FXML
    private Text nameText;

    @FXML
    private Text dateText;

    @FXML
    private VBox attemptsBox;
    private StackPane parent;
    private NameSayerShop shop;

    @FXML
    private StackPane setBadQualityButton;
    @FXML
    private StackPane unsetBadQualityButton;

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

        if (name.isBadQuality()) {
            setBadQualityButton.setVisible(false);
            unsetBadQualityButton.setVisible(true);
        } else {
            setBadQualityButton.setVisible(true);
            unsetBadQualityButton.setVisible(false);
        }
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

    @FXML
    private void onPlayClicked() {
        name.getRecording().thenAccept(AudioClip::play);
    }
}
