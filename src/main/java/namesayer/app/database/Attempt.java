package namesayer.app.database;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An interface representing an attempt in the database.
 *
 * Attempts are created when the user records a practice in the practice menu of the user interface.
 * They are associated with a list of names, which when combined form the full name being practiced.
 */
public interface Attempt extends RecordingItem<AttemptInfo> {

    /**
     * @return The time at which this attempt was made.
     */
    default LocalDateTime getAttemptTime() {
        return getInfo().getAttemptTime();
    }

    /**
     * @return The list of partial names being attempted in this attempt. For example, if the full name being practiced is
     *      "Catherine Watson", the output will be a list of "Catherine" and "Watson".
     */
    default List<String> getNames() {
        return getInfo().getNames();
    }
}
