package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

/**
 * An attempt that is based on a physical file on disk
 */
class FileBasedAttempt implements Attempt {

    private final Path location;
    private final LocalDateTime attemptTime;
    private final AudioSystem audioSystem;

    public FileBasedAttempt(Path location, LocalDateTime attemptTime, AudioSystem audioSystem) {
        this.location = location;
        this.attemptTime = attemptTime.truncatedTo(ChronoUnit.SECONDS);
        this.audioSystem = audioSystem;
    }

    /**
     * @return true if the attempt is still valid
     */
    boolean isValid() {
        return Files.exists(location);
    }

    /**
     * Delete this attempt (will invalidate)
     */
    void delete() {
        try {
            Files.delete(location);
        } catch (IOException e) {
            throw new NameSayerException("Could not delete the attempt", e);
        }
    }

    @Override
    public LocalDateTime getAttemptTime() {
        if (!isValid()) {
            throw new NameSayerException("Attempt is now invalid");
        }

        return attemptTime;
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if (!isValid()) {
            throw new NameSayerException("Attempt is now invalid");
        }

        return audioSystem.loadAudio(location);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FileBasedAttempt)) {
            return false;
        }

        FileBasedAttempt cast = (FileBasedAttempt) other;
        return cast.location.equals(this.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
