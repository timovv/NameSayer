package namesayer.app.ui;

import javafx.fxml.FXMLLoader;
import namesayer.app.NameSayerException;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

final class Util {
    static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    /**
     * Convert the given string to Title Case
     */
    static String toTitleCase(String s) {
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

    static void loadFxmlComponent(URL location, Object root) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setController(root);
        loader.setRoot(root);

        try {
            loader.load();
        } catch(IOException e) {
            throw new NameSayerException("Could not load component", e);
        }
    }
}
