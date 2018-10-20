package namesayer.app.database;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class representing information about an {@link Attempt} in the database.
 *
 * Attempts are associated with:
 * <ul>
 *     <li>A list of names, which, when combined, form the full name being practiced in the attempt.</li>
 *     <li>The time at which the attempt was created.</li>
 * </ul>
 */
public final class AttemptInfo {

    private final List<String> names;
    private final LocalDateTime attemptTime;

    /**
     * Construct a new AttemptInfo object.
     * @param names The names that this attempt is an attempt of.
     * @param attemptTime The time at which this attempt was made, which will be truncated to the second.
     */
    public AttemptInfo(List<String> names, LocalDateTime attemptTime) {
        this.names = new ArrayList<>(names);
        // Truncated here so that equals() works alright.
        this.attemptTime = attemptTime.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * When combined, these names will represent the full name that is being practiced.
     * @return A read-only view of the names this attempt is attempting.
     */
    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * @return the time at which this attempt was made.
     */
    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(names, attemptTime);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AttemptInfo)) {
            return false;
        }

        AttemptInfo other = (AttemptInfo)obj;
        return this.names.equals(other.names) && attemptTime.equals(other.attemptTime);
    }
}
