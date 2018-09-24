package namesayer.app.audio;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for different operations in the audio system.
 */
public interface AudioSystem {

    /**
     * The input level of the microphone, as an arbitrary calculation; maximum value of zero.
     */
    double getInputLevel();

    /**
     * Tell the AudioSystem to start recording.
     */
    void startRecording();

    /**
     * Tell the AudioSystem to stop recording.
     * @return The recorded AudioClip.
     */
    AudioClip stopRecording();

    /**
     * @return true if the AudioSystem is currently recording.
     */
    boolean isRecording();

    /**
     * Asynchronously load the audio clip from the given path.
     */
    CompletableFuture<AudioClip> loadAudio(Path location);

    /**
     * Asynchronously save given audio clip to the given location.
     */
    CompletableFuture<Void> saveAudio(AudioClip recording, Path location);
}
