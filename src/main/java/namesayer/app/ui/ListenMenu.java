package namesayer.app.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import namesayer.app.NameSayerException;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListenMenu extends StackPane {

    private final Parent previous;
    private final NameSayerDatabase database;
    private final NameSayerShop shop;

    @FXML
    private SearchableCellView<Name> nameCellView;

    @FXML
    private SearchableCellView<List<String>> attemptsCellView;

    public ListenMenu(Parent previous, NameSayerDatabase db, NameSayerShop shop) {
        this.previous = previous;
        this.database = db;
        this.shop = shop;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listenMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load listen menu", e);
        }
    }

    @FXML
    private void initialize() {
        Bindings.bindContent(nameCellView.getContent(), database.getNameDatabase().getAll());
        nameCellView.setCellFactory((view, value, index) -> new ListenMenuNameBlock(database, shop, value));
        nameCellView.setSearchFilter((name, searchString) -> name.getName().toLowerCase().contains(searchString.toLowerCase()));
        nameCellView.setComparator(Comparator.comparing(Name::getName, String.CASE_INSENSITIVE_ORDER));

        attemptsCellView.getContent().addAll(
                database.getAttemptDatabase().getAll().stream()
                        .collect(Collectors.<Attempt,List<String>>groupingBy(Attempt::getNames)).keySet()
        );
        attemptsCellView.setCellFactory((view, value, index) -> new ListenMenuAttemptsBlock(database, value));
        attemptsCellView.setSearchFilter((list, searchString) ->
                String.join(" ", list).toLowerCase().contains(searchString.toLowerCase()));
    }

    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
