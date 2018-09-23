package namesayer.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.database.Name;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class NameBlock extends BorderPane {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final Name name;

    @FXML
    private Text nameText;
    @FXML
    private Text dateText;

    public NameBlock(Name name, URL fxmlLocation) {
        this.name = name;
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new NameSayerException("Could not load practice menu", e);
        }

    }

    /**
     * Children should place initialize stuff in the constructor (it will work)
     */
    @FXML
    protected final void initialize() {
        nameText.setText(toTitleCase(getName().getName()));
        dateText.setText(getName().getCreationDate().format(FORMAT));
    }

    public final Name getName() {
        return name;
    }

    private String toTitleCase(String s) {
        char[] array = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean lastWhitespace = true;
        for (char c : array) {
            if (lastWhitespace) {
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
        getName().getRecording().thenAccept(AudioClip::play);
    }
}
