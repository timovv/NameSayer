package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;

public class AttemptView extends VBox {

    private ObjectProperty<Name> nameProperty = new SimpleObjectProperty<>();

    private final Label noAttemptsLabel = new Label("You haven't made any attempts for this name!");
    private final VBox attemptsVBox = new VBox();
    private ListChangeListener<Attempt> listener = c -> update();

    // don't use this to access, it is usually null
    private Name nameTemp;


    public AttemptView(Name name) {
        this.nameTemp = name;
        setName(nameTemp);
        noAttemptsLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> nameProperty.get().getAttempts().isEmpty(), nameProperty));
        getChildren().add(noAttemptsLabel);
        getChildren().add(attemptsVBox);
    }

    private void update() {
        // force updates to count etc
        nameProperty.set(nameProperty.get());
        // update the list
        attemptsVBox.getChildren().clear();
        int i = 0;
        for(Attempt attempt : nameProperty.get().getAttempts()) {
            attemptsVBox.getChildren().add(new AttemptBlock(nameProperty.get(), attempt, ++i));
        }
    }

    public void setName(Name name) {
        if(nameProperty.get() != null) {
            nameProperty.get().getAttempts().removeListener(listener);
        }
        nameProperty.set(name);
        name.getAttempts().addListener(listener);
        update();
    }

    public Name getName() {
        return nameProperty.get();
    }
}
