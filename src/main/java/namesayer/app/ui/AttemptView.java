package namesayer.app.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttemptView extends VBox {

    private final Label noAttemptsLabel = new Label("You haven't made any attempts for this name!");
    private final VBox attemptsVBox = new VBox();
    private ObjectProperty<List<Name>> namesProperty = new SimpleObjectProperty<>();
    private NameSayerDatabase database;
    private ListChangeListener<Attempt> listener = c -> update();
    private StackPane parent;

    public AttemptView(List<Name> names, NameSayerDatabase db, StackPane parent) {
        this.parent = parent;
        this.database = db;
        setNames(names);

        // If you have no attempts, show a friendly message
        noAttemptsLabel.setFont(Font.font("Century Gothic", 18));
        noAttemptsLabel.setTextFill(Paint.valueOf("#a8a8a8"));
        noAttemptsLabel.setVisible(database.getAttemptsForNames(names).isEmpty());
        noAttemptsLabel.managedProperty().bind(noAttemptsLabel.visibleProperty());

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
            attemptsVBox.getChildren().add(new AttemptBlock(database, attempt, ++i, this.parent));
        }

        // only show the no attempts label if there are no attempts!
        noAttemptsLabel.setVisible(i == 0);
    }

    public List<Name> getNames() {
        return namesProperty.get();
    }

    public void setNames(List<Name> names) {
        namesProperty.set(names);
        update();
    }
}
