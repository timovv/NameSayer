package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a playable clip of audio.
 */
public interface AudioClip {

    /**
     * Play the audio clip; the CompletableFuture returns when playback finishes.
     */
    CompletableFuture<Void> play();

    /**
     * Get the raw audio data asynchronously.
     */
    CompletableFuture<AudioData> getAudioData();
}
