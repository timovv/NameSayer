package namesayer.app.ui;

import javafx.fxml.FXML;
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
    private VBox attemptsBox;

    public ListenMenuNameBlock(NameSayerDatabase db, Name name) {
        super(name, db, ListenMenuNameBlock.class.getResource("/fxml/nameBlockMain.fxml"));
    }
}
