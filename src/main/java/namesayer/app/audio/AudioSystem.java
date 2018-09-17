package namesayer.app.audio;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface AudioSystem {
    CompletableFuture<AudioClip> recordAudio(Duration duration);

    CompletableFuture<AudioClip> loadAudio(Path location);

    CompletableFuture<Void> saveAudio(AudioClip recording, Path location);
}
