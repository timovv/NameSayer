package namesayer.app.ui;

import javafx.scene.Parent;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

public class ListenMenu extends AbstractNameView<ListenMenuNameBlock> {

    public ListenMenu(Parent previous, NameSayerDatabase db) {
        super(ListenMenu.class.getResource("/fxml/listenMenu.fxml"), previous, db);
    }

    @Override
    protected ListenMenuNameBlock createNameCell(NameSayerDatabase db, Name name) {
        return new ListenMenuNameBlock(db, name);
    }
}
