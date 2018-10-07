package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

public interface Playable {
    CompletableFuture<Void> play();

    default Playable concat(Playable other) {
        return new ConcatenatedAudio(this, other);
    }
}
