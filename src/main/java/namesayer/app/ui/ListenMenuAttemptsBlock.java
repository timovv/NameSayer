package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.database.Attempt;
import namesayer.app.database.NameSayerDatabase;

import java.io.IOException;
import java.util.List;

/**
 * A block used in the listen menu which represents a name that has been attempted.
 *
 * This block is used in the 'attempts' tab of the menu and lets the user expand and unexpand the menu to view attempts.
 */
public class ListenMenuAttemptsBlock extends BorderPane {

    /**
     * A property representing whether this block is currently expanded or not. False by default.
     */
    private BooleanProperty expanded;

    /**
     * The parent StackPane, which should be used to display dialogs to the user.
     */
    private StackPane parent;

    /**
     * When clicked, causes the block to expand to list the users' attempts.
     */
    @FXML
    private StackPane expandoExpand;

    /**
     * When clicked, causes the block to contract ('unexpand') the list of user attempts.
     */
    @FXML
    private StackPane expandoUnexpand;

    /**
     * A box showing the users' attempts for the name
     */
    @FXML
    private VBox attemptsBox;

    /**
     * The text label which displays the name to the user
     */
    @FXML
    private Text nameText;

    /**
     * Creates a new ListenMenuAttemptsBlock with the given parameters.
     * @param db The database which the attempts are from.
     * @param combinedName The name which is being attempted as a list of partial names.
     * @param parent The UI parent which is used to show JFXDialogs
     */
    public ListenMenuAttemptsBlock(NameSayerDatabase db, List<String> combinedName, StackPane parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listenMenuAttemptsBlock.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load block", e);
        }

        this.parent = parent;
        nameText.setText(String.join(" ", combinedName));
        expanded = new SimpleBooleanProperty(false);

        // show the correct (expand vs unexpand) button based on whether attempts are visible or not
        expandoExpand.visibleProperty().bind(expanded.not());
        expandoUnexpand.visibleProperty().bind(expanded);

        // only show the attempts when the component is expanded
        attemptsBox.visibleProperty().bind(expanded);
        attemptsBox.managedProperty().bind(attemptsBox.visibleProperty()); // binding managed means that height is recalculated correctly

        // Add the attempts to the attempts box.
        int i = 0;
        List<Attempt> attempts = db.getAttemptsFor(combinedName);
        for(Attempt attempt : attempts) {
            attemptsBox.getChildren().add(new AttemptBlock(db, attempt, ++i, this.parent));
        }
    }

    /**
     * Marks the menu as expanded when the expand button is clicked
     */
    @FXML
    private void onExpandClicked() {
        expanded.set(true);
    }

    /**
     * Marks the menu as collapsed ('unexpanded') when the unexpand button is clicked
     */
    @FXML
    private void onUnexpandClicked() {
        expanded.set(false);
    }
}
