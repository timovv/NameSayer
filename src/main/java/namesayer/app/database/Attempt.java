package namesayer.app.database;

import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a practice attempt at a name.
 */
public interface Attempt {

    /**
     * @return The time of an attempt.
     */
    LocalDateTime getAttemptTime();

    /**
     * Asynchronously get the recording associated with this attempt.
     */
    CompletableFuture<AudioClip> getRecording();
}
