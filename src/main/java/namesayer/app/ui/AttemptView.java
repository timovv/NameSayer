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

/**
 * A container which displays a list of the users' attempts for a given name.
 */
public class AttemptView extends VBox {

    private final Label noAttemptsLabel = new Label("You haven't made any attempts for this name!");
    private final VBox attemptsVBox = new VBox();
    private ObjectProperty<List<Name>> namesProperty = new SimpleObjectProperty<>();
    private NameSayerDatabase database;
    private ListChangeListener<Attempt> listener = c -> update();
    private StackPane parent;

    /**
     * Construct a new AttemptView with the given parameters.
     * @param names The combined name being attempted, as a list of database names.
     * @param db The database.
     * @param parent The parent StackPane; used to show JFXDialogs.
     */
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

    /**
     * Update the contents of this AttemptView in order to show new changes.
     */
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

    /**
     * @return The combined name associated with this AttemptView.
     */
    public List<Name> getNames() {
        return namesProperty.get();
    }

    /**
     * Set the combined name this AttemptView shows attempts for.
     * @param names The combined name that this AttemptView should show attempts for.
     */
    public void setNames(List<Name> names) {
        namesProperty.set(names);
        update();
    }
}
