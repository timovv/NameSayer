package namesayer.app.ui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import namesayer.app.NameSayerException;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.stream.Collectors;

public abstract class AbstractNameView<TCell extends NameBlock> extends BorderPane {

    private Parent previous;

    @FXML
    private VBox namesBox;
    @FXML
    private TextField nameSearch;

    private FilteredList<TCell> filteredNames;
    private ObservableList<TCell> allNames;

    protected AbstractNameView(URL fxmlFile, Parent previous, NameDatabase db) {

        FXMLLoader loader = new FXMLLoader(fxmlFile);
        loader.setController(this);
        loader.setRoot(this);


        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        this.previous = previous;

        allNames = FXCollections.observableArrayList(NameBlock::getObservables);
        allNames.addAll(db.getNames().stream().map(x -> createNameCell(db, x)).collect(Collectors.toList()));

        db.getNames().addListener((ListChangeListener<Name>) change -> {
            while(change.next()) {
                if(change.wasAdded()) {
                    allNames.add(createNameCell(db, change.getAddedSubList().get(0)));
                } else if(change.wasRemoved()) {
                    Name removed = change.getRemoved().get(0);
                    allNames.stream().filter(x -> x.getName().equals(removed)).findFirst()
                            .ifPresent(cell -> allNames.remove(cell));
                }
            }
        });

        filteredNames = allNames.sorted(Comparator.comparing(x -> x.getName().getName(), String.CASE_INSENSITIVE_ORDER))
                .filtered(x -> x.getName().getName().toLowerCase().contains(nameSearch.getText().toLowerCase()));

        nameSearch.textProperty().addListener((observableValue, oldSearch, newSearch) ->
                filteredNames.setPredicate(x -> x.getName().getName().toLowerCase().contains(nameSearch.getText().toLowerCase())));

        Bindings.bindContent(namesBox.getChildren(), filteredNames);
    }

    protected abstract TCell createNameCell(NameDatabase db, Name name);

    protected final ObservableList<TCell> getAllNames() {
        return allNames;
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
