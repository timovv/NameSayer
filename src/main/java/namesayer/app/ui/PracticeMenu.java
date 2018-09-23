package namesayer.app.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.awt.Button;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PracticeMenu extends AbstractNameView<PracticeMenuNameBlock> {

    private static Random random = new Random();

    private IntegerProperty selectedCount = new SimpleIntegerProperty();
    private FilteredList<PracticeMenuNameBlock> selected;
    private NameDatabase database;
    private AudioSystem audioSystem;

    @FXML
    private Text selectedText;

    @FXML
    private CheckBox shuffleCheckBox;

    public PracticeMenu(Parent previous, AudioSystem audioSystem, NameDatabase db) {
        super(PracticeMenu.class.getResource("/fxml/practiceMenu.fxml"), previous, db);

        this.audioSystem = audioSystem;
        this.database = db;
        selected = getAllNames().filtered(PracticeMenuNameBlock::isSelected);
        selectedCount.set(selected.size());
        selected.addListener((InvalidationListener) observable -> selectedCount.set(selected.size()));

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

        List<Name> names = selected.stream().map(NameBlock::getName).collect(Collectors.toList());
        if(shuffleCheckBox.isSelected()) {
            Collections.shuffle(names);
        } else {
            names.sort(Comparator.comparing(Name::getName).thenComparing(Name::getCreationDate));
        }

        reset();
        getScene().setRoot(new PracticeRecordingMenu(this, audioSystem, database, names));
    }
}
