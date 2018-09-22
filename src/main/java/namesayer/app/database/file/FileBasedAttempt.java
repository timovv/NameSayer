package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

class FileBasedAttempt implements Attempt {

    private final Path location;
    private final LocalDateTime attemptTime;
    private final AudioSystem audioSystem;

    public FileBasedAttempt(Path location, LocalDateTime attemptTime, AudioSystem audioSystem) {
        this.location = location;
        this.attemptTime = attemptTime;
        this.audioSystem = audioSystem;
    }

    boolean isValid() {
        return Files.exists(location);
    }

    void delete() {
        try {
            Files.delete(location);
        } catch(IOException e) {
            throw new NameSayerException("Could not delete the attempt", e);
        }
    }

    @Override
    public LocalDateTime getAttemptTime() {
        if(!isValid()) {
            throw new NameSayerException("Attempt is now invalid");
        }

        return attemptTime;
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if(!isValid()) {
            throw new NameSayerException("Attempt is now invalid");
        }

        return audioSystem.loadAudio(location);
    }
}
