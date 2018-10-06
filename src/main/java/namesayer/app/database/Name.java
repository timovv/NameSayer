package namesayer.app.database;

import java.time.LocalDateTime;

/**
 * Represents a name in a name database.
 */
public interface Name extends RecordingItem<NameInfo> {

    /**
     * @return The actual name that this name + recording represents.
     */
    default String getName() {
        return getInfo().getName();
    }

    /**
     * @return The date when this name was created
     */
    default LocalDateTime getCreationDate() {
        return getInfo().getCreationTime();
    }

    /**
     * @return whether this name has been marked as 'bad quality'
     */
    boolean isBadQuality();

    /**
     * Mark this name as good or bad quality.
     */
    void setBadQuality(boolean value);
}
