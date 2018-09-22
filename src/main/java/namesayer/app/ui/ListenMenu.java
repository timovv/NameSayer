package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.Comparator;
import java.util.stream.Collectors;

public class ListenMenu extends BorderPane {

    private Parent previous;

    @FXML
    private VBox namesBox;

    @FXML
    private TextField nameSearch;

    private FilteredList<UserNameBlock> names;

    public ListenMenu(Parent previous, NameDatabase db) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listenMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);


        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        this.previous = previous;

        ObservableList<UserNameBlock> userNameBlocks =
                FXCollections.observableArrayList(db.getNames().stream().map(x -> new UserNameBlock(db, x)).collect(Collectors.toList()));

        db.getNames().addListener((ListChangeListener<Name>) change -> {
            while(change.next()) {
                if(change.wasAdded()) {
                    userNameBlocks.add(new UserNameBlock(db, change.getAddedSubList().get(0)));
                } else if(change.wasRemoved()) {
                    userNameBlocks.add(new UserNameBlock(db, change.getAddedSubList().get(0)));
                }
            }
        });

        names = userNameBlocks.sorted(Comparator.comparing(x -> x.getName().getName(), String.CASE_INSENSITIVE_ORDER))
                .filtered(x -> x.getName().getName().toLowerCase().contains(nameSearch.getText().toLowerCase()));

        nameSearch.textProperty().addListener((observableValue, oldSearch, newSearch) ->
                names.setPredicate(x -> x.getName().getName().toLowerCase().contains(nameSearch.getText().toLowerCase())));

        Bindings.bindContent(namesBox.getChildren(), names);
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
