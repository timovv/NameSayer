package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

class ConcatenatedAudio implements Playable {

    private final Playable first;
    private final Playable second;

    ConcatenatedAudio(Playable first, Playable second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public CompletableFuture<Void> play() {
        return first.play().thenRun(second::play);
    }
}
