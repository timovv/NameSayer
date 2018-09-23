package namesayer.app.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

public class PracticeMenu extends AbstractNameView<PracticeMenuNameBlock> {

    private IntegerProperty selectedCount = new SimpleIntegerProperty();
    private FilteredList<PracticeMenuNameBlock> selected;

    @FXML
    private Text selectedText;

    public PracticeMenu(Parent previous, NameDatabase db) {
        super(PracticeMenu.class.getResource("/fxml/practiceMenu.fxml"), previous, db);

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
        for(PracticeMenuNameBlock block : getAllNames()) {
            block.setSelected(false);
        }
    }
}
