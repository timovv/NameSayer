package namesayer.app.ui;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameSayerDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Menu which allows the user to select what name(s) they want to practice
 */
public class PracticeMenu extends StackPane {

    private IntegerProperty selectedCount = new SimpleIntegerProperty();
    private NameSayerDatabase database;
    private AudioSystem audioSystem;

    @FXML
    private AutoCompleteTextField namesTextField;

    @FXML
    private Text selectedText;

    @FXML
    private CheckBox shuffleCheckBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private Text searchPrompt;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ListView<List<Name>> namesList;

    private final Parent previous;

    public PracticeMenu(Parent previous, AudioSystem audioSystem, NameSayerDatabase db) {

        this.audioSystem = audioSystem;
        this.database = db;
        this.previous = previous;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/practiceMenu.fxml"));

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load fxml for practice menu", e);
        }
    }

    @FXML
    private void initialize() {

        // show how many names the user has ticked
        selectedText.textProperty().bind(Bindings.concat("Selected: ", selectedCount));

        // hides the progress bar until you need it for loading.
        progressBar.setVisible(false);

        namesList.setEditable(false);
        namesList.setPlaceholder(new Label("Nothing has been selected yet!"));
        namesList.setCellFactory(param -> new ListCell<List<Name>>() {
            @Override
            protected void updateItem(List<Name> item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null || item.isEmpty()) {
                    setText(null);
                } else {
                    setText(String.join(" ",
                            item.stream().map(Name::getName).map(Util::toTitleCase).collect(Collectors.toList())));
                }
            }
        });

        selectedCount.bind(Bindings.size(namesList.getItems()));

        namesTextField.getEntries()
                .addAll(database.getNameDatabase().getAll().stream().map(Name::getName).map(Util::toTitleCase).collect(Collectors.toSet()));
    }

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

        List<List<Name>> names = new ArrayList<>(namesList.getItems());
        if (shuffleCheckBox.isSelected()) {
            Collections.shuffle(names);
        }

        // reset for when user comes back
        reset();
        getScene().setRoot(new PracticeRecordingMenu(this, audioSystem, database, names));
    }

    public void reset() {
//        namesChipView.getChips().clear();
        namesTextField.clear();
        namesList.getItems().clear();
    }

    @FXML
    private void onNameChanged() {
    }

    @FXML
    private void addNameClicked() {
        String text = namesTextField.getText().trim();
        if(!tryAddName(text)) {
            new JFXDialogHelper("Could not add name", "Could not find databsase entries for the entered name!", "Okay", this).show();
        }
    }

    @FXML
    private void addFromFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", ".txt"));

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        Path path = file.toPath();

        if(!Files.isRegularFile(path)) {
            new JFXDialogHelper("File not found", "The file could not be found", "Okay", this).show();
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch(IOException e) {
            new JFXDialogHelper("Could not read file", "The file could not be read", "Okay", this).show();
            return;
        }

        List<String> notFoundFor = new ArrayList<>();
        for(String line : lines) {
            if(!tryAddName(line)) {
                notFoundFor.add(line);
            }
        }

        if(!notFoundFor.isEmpty()) {
            new JFXDialogHelper("Could not read name(s)",
                    "Could not find database entries for the following name(s):\n"
                            + String.join("\n", notFoundFor),
                    "Okay",
                    this).show();
        }
    }

    private boolean tryAddName(String fullName) {
        List<Name> namesOut = new ArrayList<>();
        String[] split = fullName.split("[\\s-]");
        boolean couldFind = true;
        for(String name : split) {
            List<Name> found = database.getNamesFor(name);

        if(found.isEmpty()) {
            couldFind = false;
            break;
            }

            // todo make the selection of name random (ignoring bad quality files when possible)
            namesOut.add(found.get(0));
        }

        if(couldFind) {
            namesList.getItems().add(namesOut);
            return true;
        } else {
            return false;
        }
    }

    // for testing only!
    @FXML
    private void onPracticeClicked() {
//        getScene().setRoot(new PracticeRecordingMenuStub(this, audioSystem, database));
    }

    @FXML
    private void onBackClicked() {
        reset();
        getScene().setRoot(previous);
    }
}
