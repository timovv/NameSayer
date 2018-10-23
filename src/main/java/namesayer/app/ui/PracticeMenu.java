package namesayer.app.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.shop.NameSayerShop;

/**
 * Menu which allows the user to select what name(s) they want to practice.
 */
public class PracticeMenu extends StackPane {

    /**
     * The menu to return to when the back button is pressed (and when the user finishes a practiec session)
     */
    private final Parent mainMenu;

    /**
     * Property representing the number of names that have currently been selected for practice.
     */
    private IntegerProperty selectedCount = new SimpleIntegerProperty();

    /**
     * The NameSayer database useed in the application
     */
    private NameSayerDatabase database;

    /**
     * The audio system used in the application
     */
    private AudioSystem audioSystem;

    /**
     * The NameSayer shop, so that we can give the user rewards for completing practices
     */
    private NameSayerShop shop;

    /**
     * The text field in which the users can type names to be input.
     */
    @FXML
    private AutoCompleteTextField namesTextField;

    /**
     * Text label which shows the number of names that have been selected for practice
     */
    @FXML
    private Text selectedText;

    /**
     * Check box which when checked, causes the names to be shuffled when the practice session begins.
     */
    @FXML
    private CheckBox shuffleCheckBox;

    /**
     * Stack pane which allows dialogs to be shown
     */
    @FXML
    private StackPane stackPane;

    /**
     * ListView of names that are to be practiced.
     */
    @FXML
    private ListView<List<Name>> namesList;

    /**
     * Construct a new PracticeMenu with the given parameters.
     * @param mainMenu The main menu screen which is returned to when the practice session is complete
     * @param audioSystem The audio system used to make and play recordings
     * @param db The database with all the names in it
     * @param shop The shop menu for NameSayer
     */
    public PracticeMenu(Parent mainMenu, AudioSystem audioSystem, NameSayerDatabase db, NameSayerShop shop) {
        this.shop = shop;
        this.audioSystem = audioSystem;
        this.database = db;
        this.mainMenu = mainMenu;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/practiceMenu.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load fxml for practice menu", e);
        }
    }

    /**
     * Initialisation of components for the menu
     */
    @FXML
    private void initialize() {

        // show how many names the user has ticked
        selectedText.textProperty().bind(Bindings.concat("Selected: ", selectedCount));

        // don't let the user edit the names list directly (they have to use buttons etc)
        namesList.setEditable(false);
        Label placeholder = new Label("You have not yet chosen a name to practise.");
        placeholder.setFont(new Font("Century Gothic", 20));
        placeholder.setTextFill(Color.web("#b0b0b0"));
        namesList.setPlaceholder(placeholder);

        // Create name cells based on the name's text
        namesList.setCellFactory(param -> new ListCell<List<Name>>() {
            @Override
            protected void updateItem(List<Name> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                } else {
                    // make everything title case; split the names by space
                    setText(String.join(" ",
                            item.stream().map(Name::getName).map(Util::toTitleCase).collect(Collectors.toList())));
                }
            }
        });

        // count property
        selectedCount.bind(Bindings.size(namesList.getItems()));

        namesTextField.getEntries()
                .addAll(database.getNameDatabase().getAll().stream().map(Name::getName).map(Util::toTitleCase).collect(Collectors.toSet()));
        namesTextField.setOnAction(x -> addNameClicked());
    }

    /**
     * Handle when the 'start practice' button is clciked.
     */
    @FXML
    private void onStartClicked() {

        if (namesList.getItems().isEmpty()) {
            // use JFXDialogHelper to create and show a new pop-up dialog
            JFXDialogHelper dialog = new JFXDialogHelper("No Names Selected",
                    "Please find and choose at least one name to practice!", "Okay", stackPane);
            dialog.show();

            // deselect the button and intuitively prompt the user to find a name
            namesTextField.requestFocus();

            return;
        }

        // get the list of names to be practiced
        List<List<Name>> names = new ArrayList<>(namesList.getItems());
        if (shuffleCheckBox.isSelected()) {
            // shuffle them if the box is selected
            Collections.shuffle(names);
        }

        // open the practice recording menu
        getScene().setRoot(new PracticeRecordingMenu(this, mainMenu, audioSystem, database, shop, names));
    }

    /**
     * Resets the practice menu, clearing all names from the text field.
     */
    public void reset() {
        namesTextField.clear();
        namesList.getItems().clear();
    }

    /**
     * Handle the a new name being added to the practice list.
     */
    @FXML
    private void addNameClicked() {
        String text = namesTextField.getText().trim();

        if(text.isEmpty()) {
            return;
        }

        namesTextField.clear();
        if (!tryAddName(text)) {
            new JFXDialogHelper("Could Not Add Name", "Could not find database entries for the entered name!", "Okay", this).show();
        }
    }

    /**
     * Add a list of names to the practice list from a text file.
     */
    @FXML
    private void addFromFileClicked() {
        // Create the file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));

        File file = fileChooser.showOpenDialog(getScene().getWindow());

        // do nothing if no file was selected
        if(file == null) {
            return;
        }

        Path path = file.toPath();
        // only accept files - if the file was not found output an error
        if (!Files.isRegularFile(path)) {
            new JFXDialogHelper("File Not Found", "The file could not be found", "Okay", this).show();
            return;
        }

        // try and read each line from the text file.
        // if a binary file, etc., is tried to be read, this will throw IOException and an error message will be shown
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            new JFXDialogHelper("Could Not Read File", "The file could not be read", "Okay", this).show();
            return;
        }

        // show a list for which the names could not be found
        List<String> notFoundFor = new ArrayList<>();
        for (String line : lines) {
            if (!tryAddName(line)) {
                notFoundFor.add(line);
            }
        }

        // if there were names that could not be found then show which ones couldn't be resolved
        if (!notFoundFor.isEmpty()) {
            new JFXDialogHelper("Could Not Read Name(s)",
                    "Could not find database entries for the following name(s):\n"
                            + String.join("\n", notFoundFor),
                    "Okay",
                    this).show();
        }
    }

    /**
     * Try to add the given name to the list of names to practice.
     * @param fullName The name to test, either from a file or the text box
     * @return true if the addition was successful; false otherwise.
     */
    private boolean tryAddName(String fullName) {
        List<Name> namesOut = new ArrayList<>();
        String[] split = fullName.split("[\\s-]");
        boolean couldFind = true;
        for (String name : split) {
            List<Name> found = database.getNamesFor(name);

            if (found.isEmpty()) {
                couldFind = false;
                break;
            }

            // If there are any names which match which are NOT bad quality, only select from non-bad-quality names.
            // Otherwise, just use the bad quality names anyway.
            if(found.stream().anyMatch(x -> !x.isBadQuality())) {
                found = found.stream().filter(x -> !x.isBadQuality()).collect(Collectors.toList());
            }

            // Shuffle the list and select the first one, this makes the selection of name random.
            Collections.shuffle(found);
            namesOut.add(found.get(0));
        }

        if (couldFind) {
            // Don't add it again if there's a duplicate
            if(!nameIsAlreadyAdded(namesOut.stream().map(Name::getName).collect(Collectors.toList()))) {
                namesList.getItems().add(namesOut);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine if the given list of names, making up a full name, has already been added to the list of names to practice.
     * @param names The names to check.
     * @return true if the name has already been added to the list of names to practice.
     */
    private boolean nameIsAlreadyAdded(List<String> names) {
        return namesList.getItems().stream()
                .map(x -> x.stream().map(Name::getName).collect(Collectors.toList())) // stringify all the names
                .anyMatch(x -> x.equals(names)); // list equality -- returns true if all elements are the same
    }

    /**
     * Go to the main menu when the back button is clicked.
     */
    @FXML
    private void onBackClicked() {
        reset();
        getScene().setRoot(mainMenu);
    }
}
