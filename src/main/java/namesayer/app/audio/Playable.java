package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

/**
 * An interface representing an audio object that can be played.
 */
public interface Playable {

    /**
     * Play this audio using the default output device.
     *
     * @return A CompletableFuture that will complete once the audio has finished playing.
     */
    CompletableFuture<Void> play();

    /**
     * Concatenate this audio to another Playable object.
     *
     * @param other The audio to play after this one.
     * @return A Playable object, which, when
     */
    default Playable concat(Playable other) {
        return new ConcatenatedAudio(this, other);
    }
}
