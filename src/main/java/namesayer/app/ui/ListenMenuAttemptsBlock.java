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

public class ListenMenuAttemptsBlock extends BorderPane {

    @FXML
    private StackPane expandoExpand;

    @FXML
    private StackPane expandoUnexpand;

    @FXML
    private VBox attemptsBox;

    @FXML
    private Text nameText;

    private BooleanProperty expanded;

    public ListenMenuAttemptsBlock(NameSayerDatabase db, List<String> combinedName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listenMenuAttemptsBlock.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load block", e);
        }

        nameText.setText(String.join(" ", combinedName));
        expanded = new SimpleBooleanProperty(false);

        // show the correct (expand vs unexpand) button based on whether attempts are visible or not
        expandoExpand.visibleProperty().bind(expanded.not());
        expandoUnexpand.visibleProperty().bind(expanded);

        attemptsBox.visibleProperty().bind(expanded);
        attemptsBox.managedProperty().bind(attemptsBox.visibleProperty()); // binding managed means that height is recalculated correctly

        int i = 0;
        List<Attempt> attempts = db.getAttemptsFor(combinedName);
        for(Attempt attempt : attempts) {
            attemptsBox.getChildren().add(new AttemptBlock(db, attempt, ++i));
        }
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
