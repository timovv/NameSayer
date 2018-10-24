package namesayer.app.database;

import namesayer.app.audio.AudioClip;

import java.util.concurrent.CompletableFuture;

/**
 * An entry in a {@link RecordingDatabase}.
 *
 * @param <TInfo> The information object which provides data about the recording.
 */
public interface RecordingItem<TInfo> {

    /**
     * @return The information object associated with this RecordingItem.
     */
    TInfo getInfo();

    /**
     * @return A CompletableFuture object which will yield the AudioClip corresponding to this recording.
     */
    CompletableFuture<AudioClip> getRecording();
}
