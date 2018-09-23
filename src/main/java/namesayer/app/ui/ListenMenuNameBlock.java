package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.time.format.DateTimeFormatter;

public class ListenMenuNameBlock extends NameBlock {


    @FXML
    private Text nameText;

    @FXML
    private Text dateText;

    @FXML
    private StackPane expandoExpand;

    @FXML
    private StackPane expandoUnexpand;

    private BooleanProperty expanded;

    private final NameDatabase database;

    public ListenMenuNameBlock(NameDatabase db, Name name) {
        super(name, db, ListenMenuNameBlock.class.getResource("/fxml/nameBlockMain.fxml"));
        this.database = db;
        expanded = new SimpleBooleanProperty(false);

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
