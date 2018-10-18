package namesayer.app.ui;

import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
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

public class SearchableCellView<T> extends VBox {

    private final TextField searchBox;
    private final VBox insidePaneBox;
    private final ObservableList<T> content;
    private final SortedList<T> contentSorted;
    private final FilteredList<T> contentFiltered;
    private final ScrollPane scrollablePane;

    private Node placeholderNode;

    private ObjectProperty<CellFactory<T>> cellFactory;
    private ObjectProperty<BiPredicate<T, String>> searchFilter;

    public SearchableCellView() {
        this(new Label("No items found."));
    }

    public SearchableCellView(@NamedArg("placeholder") Node placeholder) {
        this.searchFilter = new SimpleObjectProperty<>();
        this.cellFactory = new SimpleObjectProperty<>();
        this.placeholderNode = placeholder;
        StackPane.setAlignment(placeholderNode, Pos.CENTER);

        searchBox = new TextField();
        searchBox.setPromptText("Search");
        StackPane stackPane = new StackPane();
        scrollablePane = new ScrollPane();
        scrollablePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollablePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        insidePaneBox = new VBox();
        scrollablePane.setContent(insidePaneBox);
        stackPane.getChildren().add(scrollablePane);
        placeholderNode.setVisible(false);
        stackPane.getChildren().add(placeholderNode);

        content = FXCollections.observableArrayList();
        contentSorted = content.sorted();
        contentFiltered = contentSorted.filtered(this::filterPredicate);

        searchBox.setFont(Font.font("Century Gothic", 20));
        getChildren().add(searchBox);
        getChildren().add(stackPane);

        contentFiltered.addListener((Observable x) -> refreshContent());


        this.cellFactory.addListener((Observable x) -> refreshContent());
        searchBox.textProperty().addListener((Observable x) -> refreshFilter());
        this.searchFilter.addListener((Observable x) -> refreshFilter());
    }

    private static <T> CellFactory<T> memoize(final CellFactory<T> in) {
        return new CellFactory<T>() {
            private final Map<T, Node> memo = new HashMap<>();

            @Override
            public Node createCell(SearchableCellView<T> view, T value, int index) {
                return memo.computeIfAbsent(value, t -> in.createCell(view, value, index));
            }
        };
    }

    private void refreshContent() {
        // just regenerate everything, this can be optimized to only regenerate necessary items if needed
        insidePaneBox.getChildren().clear();

        if (getCellFactory() == null || getSearchFilter() == null) {
            return;
        }

        if (contentFiltered.isEmpty()) {
            placeholderNode.setVisible(true);
            scrollablePane.setVisible(false);
            return;
        }

        placeholderNode.setVisible(false);
        scrollablePane.setVisible(true);

        int i = 0;
        for (T item : contentFiltered) {
            insidePaneBox.getChildren().add(getCellFactory().createCell(this, item, ++i));
        }
    }

    private void refreshFilter() {
        contentFiltered.setPredicate(t -> searchFilter.get().test(t, searchBox.getText()));
    }

    private boolean filterPredicate(T value) {
        // If no cell factory or filter has been defined, then pretend that there are no matches.
        return searchFilter.isBound()
                && cellFactory.isBound()
                && searchFilter.get().test(value, searchBox.getText());
    }

    public ObservableList<T> getContent() {
        return content;
    }

    public TextField getSearchBox() {
        return searchBox;
    }

    public CellFactory<T> getCellFactory() {
        return cellFactory.get();
    }

    public void setCellFactory(CellFactory<T> value) {
        cellFactory.set(memoize(value));
    }

    public ObjectProperty<CellFactory<T>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setComparator(Comparator<T> comparator) {
        contentSorted.setComparator(comparator);
    }

    public BiPredicate<T, String> getSearchFilter() {
        return searchFilter.get();
    }

    public void setSearchFilter(BiPredicate<T, String> filter) {
        searchFilter.setValue(filter);
    }

    public ObjectProperty<BiPredicate<T, String>> searchFilterProperty() {
        return searchFilter;
    }

    @FunctionalInterface
    public interface CellFactory<T> {
        Node createCell(SearchableCellView<T> view, T value, int index);
    }
}
