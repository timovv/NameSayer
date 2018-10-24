package namesayer.app.audio;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * An stub audio system that does not do anything.
 * <p>
 * Recording, input level, etc. is not available.
 */
public class AudioStub implements AudioSystem {

    @Override
    public double getInputLevel() {
        return 0;
    }

    @Override
    public void startRecording() {

    }

    @Override
    public AudioClip stopRecording() {
        return null;
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public CompletableFuture<AudioClip> loadAudio(Path location) {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveAudio(AudioClip recording, Path location) {
        return null;
    }
}
