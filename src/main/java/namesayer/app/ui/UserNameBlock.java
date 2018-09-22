package namesayer.app.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UserNameBlock extends BorderPane {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private Name name;

    @FXML
    private Text nameText;

    @FXML
    private Text dateText;

    @FXML
    private StackPane expandoExpand;

    @FXML
    private StackPane expandoUnexpand;

    private BooleanProperty expanded = new SimpleBooleanProperty(false);

    private final NameDatabase database;

    public UserNameBlock(NameDatabase db, Name name) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nameBlockMain.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

        this.name = name;
        nameText.setText(toTitleCase(name.getName()));
        dateText.setText(name.getCreationDate().format(FORMAT));

        this.database = db;
    }

    @FXML
    private void initialize() {
        expandoExpand.visibleProperty().bind(expanded.not());
        expandoUnexpand.visibleProperty().bind(expanded);
    }

    private String toTitleCase(String s) {
        char[] array = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean lastWhitespace = true;
        for(char c : array) {
            if(lastWhitespace) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(Character.toLowerCase(c));
            }

            lastWhitespace = Character.isWhitespace(c);
        }

        return sb.toString();
    }

    @FXML
    private void onPlayClicked() {
        name.getRecording().thenAccept(AudioClip::play);
    }

    @FXML
    private void onExpandClicked() {
        expanded.set(true);
    }

    @FXML
    private void onUnexpandClicked() {
        expanded.set(false);
    }

    public Name getName() {
        return name;
    }
}
