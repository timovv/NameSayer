package namesayer.app.ui;

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
}