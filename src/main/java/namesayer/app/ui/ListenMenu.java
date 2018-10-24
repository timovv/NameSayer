package namesayer.app.ui;

import javafx.beans.InvalidationListener;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the listen menu, where users can listen to recordings from the database and their previous
 * attempts.
 */
public class ListenMenu extends StackPane {

    /**
     * The menu to go to when the 'back' button is pressed.
     */
    private final Parent previous;

    /**
     * The database to listen to
     */
    private final NameSayerDatabase database;

    /**
     * The shop - used to give the user coins when they 'thumbs down' a recording
     */
    private final NameSayerShop shop;

    /**
     * The view for viewing names
     */
    @FXML
    private SearchableCellView<Name> nameCellView;

    /**
     * The view for viewing user attempts
     */
    @FXML
    private SearchableCellView<List<String>> attemptsCellView;

    /**
     * Create a new ListenMenu with the given parameters.
     *
     * @param previous The menu to go to when the back button is pressed.
     * @param db       The database being listened to.
     * @param shop     The shop; used to give the user coins when a recording is marked as bad quality.
     */
    public ListenMenu(Parent previous, NameSayerDatabase db, NameSayerShop shop) {
        this.previous = previous;
        this.database = db;
        this.shop = shop;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listenMenu.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load listen menu", e);
        }
    }

    /**
     * Used to set up FXML components
     */
    @FXML
    private void initialize() {
        Bindings.bindContent(nameCellView.getContent(), database.getNameDatabase().getAll());

        // Set up the name view to show a ListenMenuNameBlock
        nameCellView.setCellFactory((view, value) -> new ListenMenuNameBlock(database, shop, value, this));
        // Search is based on contains
        nameCellView.setSearchFilter((name, searchString) -> name.getName().toLowerCase().contains(searchString.toLowerCase()));
        // sort items in the view in case-insensitive order by name
        nameCellView.setComparator(Comparator.comparing(Name::getName, String.CASE_INSENSITIVE_ORDER));

        // Refresh the attempts view now, and every time attempts are added or removed
        refreshAttempts();
        database.getAttemptDatabase().getAll().addListener((InvalidationListener) observable -> refreshAttempts());

        // Use ListenMenuAttemptsBlock to show attempts
        attemptsCellView.setCellFactory((view, value) -> new ListenMenuAttemptsBlock(database, value, this), false);
        attemptsCellView.setSearchFilter((list, searchString) ->
                String.join(" ", list).toLowerCase().contains(searchString.toLowerCase()));
    }

    /**
     * Refreshes the attempt view, clearing it and adding all attempts again.
     */
    private void refreshAttempts() {
        attemptsCellView.getContent().clear();
        // Groups all attempts by the full name.
        attemptsCellView.getContent().addAll(
                database.getAttemptDatabase().getAll().stream()
                        .collect(Collectors.<Attempt, List<String>>groupingBy(Attempt::getNames)).keySet()
        );
    }

    /**
     * Returns the user to the previous menu
     */
    @FXML
    private void onBackClicked() {
        getScene().setRoot(previous);
    }
}
