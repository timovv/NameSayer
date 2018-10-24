package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.util.concurrent.CompletableFuture;

/**
 * A generic database of recordings.
 *
 * @param <TInfo> The type of recording information.
 * @param <TItem> The type of recording that is in this database.
 */
public interface RecordingDatabase<TInfo, TItem extends RecordingItem<TInfo>> {

    /**
     * @return All recordings in this database.
     */
    ObservableList<TItem> getAll();

    /**
     * Asynchronously a new recording of the given type in the database.
     *
     * @param info      The info corresponding to the recording.
     * @param recording The audio clip representing the recording.
     * @return A CompletableFuture object that will yield the created database entry once it has been created.
     */
    CompletableFuture<TItem> createNew(TInfo info, AudioClip recording);

    /**
     * Remove the given entry from the database.
     *
     * @param item The item to remove.
     * @return A CompleteableFuture object that will finish once the entry has been removed.
     */
    CompletableFuture<Void> remove(RecordingItem<TInfo> item);
}
