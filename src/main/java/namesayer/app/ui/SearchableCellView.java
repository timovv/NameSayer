package namesayer.app.ui;

import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * A generic class to view and search cells.
 * Used in the listen menu for search functionality.
 * @param <T> The type of the items in the SearchableCellView.
 */
public class SearchableCellView<T> extends VBox {

    /**
     * The text field used to search
     */
    private final TextField searchBox;

    /**
     * VBox which is inside the ScrollPane. Where items are placed.
     */
    private final VBox insidePaneBox;

    /**
     * List of content model items.
     */
    private final ObservableList<T> content;

    /**
     * Sorted view of the content
     */
    private final SortedList<T> contentSorted;

    /**
     * View of the content, filtered by the search term
     */
    private final FilteredList<T> contentFiltered;

    /**
     * ScrollPane for search items.
     */
    private final ScrollPane scrollablePane;

    /**
     * Node used as a placeholder if there are no items
     */
    private Node placeholderNode;

    /**
     * CellFactory that creates cells from items in the view
     */
    private ObjectProperty<CellFactory<T>> cellFactory;

    /**
     * Predicate which determines whether an item is filtered by the search, given the input search query.
     */
    private ObjectProperty<BiPredicate<T, String>> searchFilter;

    /**
     * Create a new SearchableCellView.
     */
    public SearchableCellView() {
        this(new Label("No items found."));
    }

    /**
     * Create a new SearchableCellView with the given placeholder node.
     * @param placeholder The placeholder node to use.
     */
    public SearchableCellView(@NamedArg("placeholder") Node placeholder) {
        this.searchFilter = new SimpleObjectProperty<>();
        this.cellFactory = new SimpleObjectProperty<>();
        this.placeholderNode = placeholder;

        // set up contents of the cell
        StackPane.setAlignment(placeholderNode, Pos.CENTER);

        // search text field
        searchBox = new TextField();
        searchBox.setPromptText("Search");
        StackPane stackPane = new StackPane();
        scrollablePane = new ScrollPane();
        scrollablePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollablePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        // vbox
        insidePaneBox = new VBox();
        scrollablePane.setContent(insidePaneBox);
        stackPane.getChildren().add(scrollablePane);
        placeholderNode.setVisible(false);
        stackPane.getChildren().add(placeholderNode);

        // content
        content = FXCollections.observableArrayList();
        contentSorted = content.sorted();
        contentFiltered = contentSorted.filtered(this::filterPredicate);

        searchBox.setFont(Font.font("Century Gothic", 20));
        getChildren().add(searchBox);
        getChildren().add(stackPane);

        // refresh the content as required
        contentFiltered.addListener((Observable x) -> refreshContent());
        this.cellFactory.addListener((Observable x) -> refreshContent());
        searchBox.textProperty().addListener((Observable x) -> refreshFilter());
        this.searchFilter.addListener((Observable x) -> refreshFilter());
    }

    /**
     * Function which given a CellFactory gives a cellfactory that caches values.
     * @param in The cellFactory input
     * @param <T> The type of the cell factory
     * @return A cell factory which does not recompute if the same object is encountered
     */
    private static <T> CellFactory<T> memoize(final CellFactory<T> in) {
        return new CellFactory<T>() {
            private final Map<T, Node> memo = new HashMap<>();

            @Override
            public Node createCell(SearchableCellView<T> view, T value) {
                return memo.computeIfAbsent(value, t -> in.createCell(view, value));
            }
        };
    }

    /**
     * Refresh everything in the cell view
     */
    private void refreshContent() {
        // just regenerate everything, this can be optimized to only regenerate necessary items if needed
        insidePaneBox.getChildren().clear();

        if (getCellFactory() == null || getSearchFilter() == null) {
            return;
        }

        // if content is empty show the placeholder
        if (contentFiltered.isEmpty()) {
            placeholderNode.setVisible(true);
            scrollablePane.setVisible(false);
            return;
        }

        placeholderNode.setVisible(false);
        scrollablePane.setVisible(true);

        int i = 0;
        for (T item : contentFiltered) {
            insidePaneBox.getChildren().add(getCellFactory().createCell(this, item));
        }
    }

    /**
     * Updates the filter predicate for when the search box text changes
     */
    private void refreshFilter() {
        contentFiltered.setPredicate(t -> searchFilter.get().test(t, searchBox.getText()));
    }

    /**
     * Useful default predicate which returns always returns false if hte cell factory or search filter has not been set.
     * @param value The value to test
     * @return true if the search filter matches the value, false otherwise. Also returns false if the cell factory
     * or search filter is unset.
     */
    private boolean filterPredicate(T value) {
        // If no cell factory or filter has been defined, then pretend that there are no matches.
        return searchFilter.isBound()
                && cellFactory.isBound()
                && searchFilter.get().test(value, searchBox.getText());
    }

    /**
     * @return The content of this SearchableCellView
     */
    public ObservableList<T> getContent() {
        return content;
    }

    /**
     * @return the search box behind this SearchableCellView
     */
    public TextField getSearchBox() {
        return searchBox;
    }

    /**
     * @return The CellFactory used by this SearchableCellView
     */
    public CellFactory<T> getCellFactory() {
        return cellFactory.get();
    }

    /**
     * Set the cell factory to the given value. Caching is applied so that if the same object is passed through the
     * factory, a cached value is used.
     * @param value The value to set the cell factory to
     */
    public void setCellFactory(CellFactory<T> value) {
        setCellFactory(value, true);
    }

    /**
     * Set the cell factory to the given value.
     *
     * @param value The value to set the cell factory to.
     * @param cache Set this to true to cache values so that if the same object is passed through the factory,
     *              a previously calculated value will be used. Setting this to false will cause the value to be
     *              recalculated every time the factory is used, which may cause performance issues.
     */
    public void setCellFactory(CellFactory<T> value, boolean cache) {
        cellFactory.set(cache ? memoize(value) : value);
    }

    /**
     * @return a read-only JavaFX property representing the current cell factory. To set the cell factory, use setCellFactory.
     */
    public ReadOnlyObjectProperty<CellFactory<T>> cellFactoryProperty() {
        return cellFactory;
    }

    /**
     * Items in the SearchableCellView are ordered. This method sets the comparator used to order the items.
     * @param comparator The comparator used to order the items.
     */
    public void setComparator(Comparator<T> comparator) {
        contentSorted.setComparator(comparator);
    }

    /**
     * @return A predicate used to determine whether a given object matches the input search query.
     */
    public BiPredicate<T, String> getSearchFilter() {
        return searchFilter.get();
    }

    /**
     * Set the search filter used by this SearchableCellView.
     * @param filter The search filter to use. Given the input object and a search query, the filter should return
     *               true if the object should appear in search results.
     */
    public void setSearchFilter(BiPredicate<T, String> filter) {
        searchFilter.setValue(filter);
    }

    /**
     * @return A JavaFX property representing the search filter.
     */
    public ObjectProperty<BiPredicate<T, String>> searchFilterProperty() {
        return searchFilter;
    }

    /**
     * An interface representing a cell factory, used to create cells for the SearchableCellView.
     * @param <T> The type of the items in the cell view.
     */
    @FunctionalInterface
    public interface CellFactory<T> {

        /**
         * Create a new cell based on the given object in the SearchableCellView.
         * @param view The cell view for which the cell is being created.
         * @param value The value to create the cell for.
         * @return The created cell.
         */
        Node createCell(SearchableCellView<T> view, T value);
    }
}
