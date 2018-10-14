package namesayer.app.ui;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

/**
 * A name block with a check box as used by {@link PracticeRecordingMenu}
 */
public class PracticeMenuNameBlock extends NameBlock {

    @FXML
    private CheckBox selectedCheckBox;

    public PracticeMenuNameBlock(NameSayerDatabase db, Name name) {
        super(name, db, PracticeMenuNameBlock.class.getResource("/fxml/nameBlockUser.fxml"));
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

    @Override
    public Observable[] getObservables() {
        return new Observable[]{
                selectedProperty()
        };
    }
}
