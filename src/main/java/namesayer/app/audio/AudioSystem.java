package namesayer.app.audio;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface AudioSystem {

    double getInputLevel();

    void startRecording();

    AudioClip stopRecording();

    boolean isRecording();

    CompletableFuture<AudioClip> loadAudio(Path location);

    CompletableFuture<Void> saveAudio(AudioClip recording, Path location);
}
