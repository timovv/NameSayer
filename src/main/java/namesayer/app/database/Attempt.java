package namesayer.app.database;

import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface Attempt {

    LocalDateTime getAttemptTime();

    CompletableFuture<AudioClip> getRecording();
}
