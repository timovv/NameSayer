package namesayer.app.database;

import namesayer.app.audio.AudioClip;

import java.util.concurrent.CompletableFuture;

public interface RecordingItem<TInfo> {
    TInfo getInfo();

    CompletableFuture<AudioClip> getRecording();
}
