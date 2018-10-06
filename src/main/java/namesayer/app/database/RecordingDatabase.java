package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.util.concurrent.CompletableFuture;

public interface RecordingDatabase<TInfo, TItem extends RecordingItem<TInfo>> {

    ObservableList<TItem> getAll();
    
    CompletableFuture<TItem> createNew(TInfo info, AudioClip recording);

    CompletableFuture<Void> remove(RecordingItem<TInfo> item);
}
