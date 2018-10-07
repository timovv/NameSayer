package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttemptView extends VBox {

    private ObjectProperty<List<Name>> namesProperty = new SimpleObjectProperty<>();

    private final Label noAttemptsLabel = new Label("You haven't made any attempts for this name!");
    private final VBox attemptsVBox = new VBox();
    private ListChangeListener<Attempt> listener = c -> update();
    private NameSayerDatabase database;

    public AttemptView(List<Name> names, NameSayerDatabase db) {

        this.database = db;
        setNames(names);
        // If you have no attempts, show a friendly message
        noAttemptsLabel.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                db.getAttemptsForNames(namesProperty.get()).isEmpty(),
                namesProperty));

        getChildren().add(noAttemptsLabel);
        getChildren().add(attemptsVBox);
        database.getAttemptDatabase().getAll().addListener(listener);
    }

    private void update() {
        // forced property update
        namesProperty.set(namesProperty.get());
        // update the list
        attemptsVBox.getChildren().clear();
        int i = 0;
        for (Attempt attempt : database.getAttemptsForNames(namesProperty.get())
                .stream().sorted(Comparator.comparing(Attempt::getAttemptTime)).collect(Collectors.toList())) {
            attemptsVBox.getChildren().add(new AttemptBlock(database, attempt, ++i));
        }
    }

    public void setNames(List<Name> names) {
        namesProperty.set(names);
        update();
    }

    public List<Name> getNames() {
        return namesProperty.get();
    }
}
