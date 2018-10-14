package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

/**
 * A NameBlock that is used in the listen menu, and has an 'expando' that lets you view your attempts
 */
public class ListenMenuNameBlock extends NameBlock {


    @FXML
    private Text nameText;

    @FXML
    private Text dateText;

    @FXML
    private StackPane expandoExpand;

    @FXML
    private StackPane expandoUnexpand;

    @FXML
    private VBox attemptsBox;

    private BooleanProperty expanded;

    public ListenMenuNameBlock(NameSayerDatabase db, Name name) {
        super(name, db, ListenMenuNameBlock.class.getResource("/fxml/nameBlockMain.fxml"));
        expanded = new SimpleBooleanProperty(false);

        // show the correct (expand vs unexpand) button based on whether attempts are visible or not
        expandoExpand.visibleProperty().bind(expanded.not());
        expandoUnexpand.visibleProperty().bind(expanded);
    }

    @FXML
    private void onExpandClicked() {
        expanded.set(true);
    }

    @FXML
    private void onUnexpandClicked() {
        expanded.set(false);
    }
}
