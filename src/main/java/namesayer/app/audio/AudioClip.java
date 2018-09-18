package namesayer.app.audio;

import java.util.concurrent.CompletableFuture;

public interface AudioClip {

    CompletableFuture<Void> play();

    CompletableFuture<AudioData> getAudioData();
}
