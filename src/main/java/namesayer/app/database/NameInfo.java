package namesayer.app.database;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * A class representing information about a {@link Name} in the database.
 *
 * Names are associated with:
 * <ul>
 *     <li>The string representing the name itself, e.g. "Catherine"</li>
 *     <li>The string representing the creator of the name. For most cases, this is "se206".</li>
 *     <li>The time at which the name was created.</li>
 * </ul>
 */
public final class NameInfo {
    private final String name;
    private final String creator;
    private final LocalDateTime creationDate;

    /**
     * Construct a new NameInfo object.
     * @param name The name the recording is a recording of.
     * @param creator The creator of the name.
     * @param creationDate The date when this recording was created.
     */
    public NameInfo(String name, String creator, LocalDateTime creationDate) {
        this.name = name;
        this.creator = creator;

        // truncate this to seconds since that's all we want to be accurate to
        this.creationDate = creationDate.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * @return The name that is associated with the recording in the database.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The name of the creator of the recording. In almost all cases in the supplied database, this is "se206".
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @return The time that this recording was created.
     */
    public LocalDateTime getCreationTime() {
        return creationDate;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof NameInfo)) {
            return false;
        }

        NameInfo otherNameInfo = (NameInfo) other;
        return otherNameInfo.name.equals(this.name) && otherNameInfo.creationDate.equals(creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, creator, creationDate);
    }
}
