package namesayer.app.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu which allows the user to select what name(s) they want to practice
 */
public class PracticeMenu extends AbstractNameView<PracticeMenuNameBlock> {

    private IntegerProperty selectedCount = new SimpleIntegerProperty();
    private FilteredList<PracticeMenuNameBlock> selected;
    private NameDatabase database;
    private AudioSystem audioSystem;

    @FXML
    private Text selectedText;

    @FXML
    private CheckBox shuffleCheckBox;

    @FXML
    private StackPane stackPane;

    public PracticeMenu(Parent previous, AudioSystem audioSystem, NameDatabase db) {
        super(PracticeMenu.class.getResource("/fxml/practiceMenu.fxml"), previous, db);

        this.audioSystem = audioSystem;
        this.database = db;
        selected = getAllNames().filtered(PracticeMenuNameBlock::isSelected);
        selectedCount.set(selected.size());
        selected.addListener((InvalidationListener) observable -> selectedCount.set(selected.size()));

        // show how many names the user has ticked
        selectedText.textProperty().bind(Bindings.concat("Selected: ", selectedCount));
    }

    @Override
    protected PracticeMenuNameBlock createNameCell(NameDatabase db, Name name) {
        return new PracticeMenuNameBlock(db, name);
    }

    public void reset() {
        for (PracticeMenuNameBlock block : getAllNames()) {
            block.setSelected(false);
        }
    }

    @FXML
    private void onStartClicked() {

        if (selected.isEmpty()) {
            // use JFXDialogHelper to create and show a new pop-up dialog
            JFXDialogHelper dialog = new JFXDialogHelper("No Names Selected",
                    "Please find and choose at least one name to practice!", "Okay", stackPane);
            dialog.show();

            // deselect the button and intuitively prompt the user to find a name
            this.getNamesSearch().requestFocus();

            return;
        }

        List<Name> names = selected.stream().map(NameBlock::getName).collect(Collectors.toList());

        if (shuffleCheckBox.isSelected()) {
            Collections.shuffle(names);
        } else {
            // if not shuffling, present them in the same order as in the NameView
            names.sort(Comparator.comparing(Name::getName).thenComparing(Name::getCreationDate));
        }

        // reset for when user comes back
        reset();
        getScene().setRoot(new PracticeRecordingMenu(this, audioSystem, database, names));
    }

    // for testing only!
    @FXML
    private void onPracticeClicked() {
        getScene().setRoot(new PracticeRecordingMenuStub(this, audioSystem, database));
    }
}
