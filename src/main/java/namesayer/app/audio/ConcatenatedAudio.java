package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

/**
 * An implementation of Playable that plays two audio clips together, one after the other.
 */
class ConcatenatedAudio implements Playable {

    private final Playable first;
    private final Playable second;

    /**
     * Create a new ConcatenatedAudio object.
     *
     * @param first  The audio to play first.
     * @param second The audio to play second.
     */
    ConcatenatedAudio(Playable first, Playable second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Play the first piece of audio, then the second.
     */
    @Override
    public CompletableFuture<Void> play() {
        return first.play().thenRun(second::play);
    }
}
