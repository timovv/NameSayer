package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

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

    private final NameDatabase database;
    private final Name name;

    public ListenMenuNameBlock(NameDatabase db, Name name) {
        super(name, db, ListenMenuNameBlock.class.getResource("/fxml/nameBlockMain.fxml"));
        this.database = db;
        this.name = name;
        expanded = new SimpleBooleanProperty(false);

        expandoExpand.visibleProperty().bind(expanded.not());
        expandoUnexpand.visibleProperty().bind(expanded);
    }

    @FXML
    private void onExpandClicked() {
        expanded.set(true);

        int i = 0;
        for(Attempt attempt : name.getAttempts()) {
            attemptsBox.getChildren().add(new AttemptBlock(name, attempt, ++i));
        }
    }

    @FXML
    private void onUnexpandClicked() {
        expanded.set(false);
        attemptsBox.getChildren().clear();
    }
}
