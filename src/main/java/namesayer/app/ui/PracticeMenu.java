package namesayer.app.ui;

import javafx.scene.Parent;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

public class PracticeMenu extends AbstractNameView<PracticeMenuNameBlock> {

    private Parent previous;

    public PracticeMenu(Parent previous, NameDatabase db) {
        super(PracticeMenu.class.getResource("/fxml/practiceMenu.fxml"), previous, db);
    }

    @Override
    protected PracticeMenuNameBlock getNameCell(NameDatabase db, Name name) {
        return new PracticeMenuNameBlock(db, name);
    }
}
