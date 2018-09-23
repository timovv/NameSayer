package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

public class PracticeMenuNameBlock extends NameBlock {

    private final NameDatabase db;

    @FXML
    private CheckBox selectedCheckBox;

    public PracticeMenuNameBlock(NameDatabase db, Name name) {
        super(name, PracticeMenuNameBlock.class.getResource("/fxml/nameBlockUser.fxml"));
        this.db = db;
    }

    public BooleanProperty selectedProperty() {
        return selectedCheckBox.selectedProperty();
    }

    public boolean isSelected() {
        return selectedCheckBox.isSelected();
    }

    public void setSelected(boolean value) {
        selectedCheckBox.setSelected(value);
    }
}
