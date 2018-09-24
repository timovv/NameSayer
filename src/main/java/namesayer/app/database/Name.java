package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;


/**
 * Represents a name in a name database.
 */
public interface Name {

    /**
     * @return The actual name that this name + recording represents.
     */
    default String getName() {
        return getNameInfo().getName();
    }

    /**
     * @return The date when this name was created
     */
    default LocalDateTime getCreationDate() {
        return getNameInfo().getCreationTime();
    }

    /**
     * @return The NameInfo associated with this Name.
     */
    NameInfo getNameInfo();

    /**
     * @return the recording associated with this name
     */
    CompletableFuture<AudioClip> getRecording();

    /**
     * @return whether this name has been marked as 'bad quality'
     */
    boolean isBadQuality();

    /**
     * Mark this name as good or bad quality.
     */
    void setBadQuality(boolean value);

    /**
     * @return A read-only collection of all attempts associated with this name.
     */
    ObservableList<Attempt> getAttempts();

    /**
     * Add an attempt to this name.
     */
    CompletableFuture<Void> addAttempt(AudioClip recording, LocalDateTime creationTime);

    /**
     * Remove the given attempt from this name.
     */
    CompletableFuture<Void> removeAttempt(Attempt toRemove);
}
