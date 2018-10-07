package namesayer.app.database.file;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.RecordingDatabase;
import namesayer.app.database.RecordingItem;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A name database that is based on a flat file
 */
public class FileBasedRecordingDatabase<TInfo, TRecording extends FileBasedRecordingItem<TInfo>>
        implements RecordingDatabase<TInfo, TRecording> {

    private final ObservableList<TRecording> recordings = FXCollections.observableArrayList();
    private final Map<TInfo, TRecording> lookup = new HashMap<>();
    private final ObservableList<TRecording> recordingsReadOnly = FXCollections.unmodifiableObservableList(recordings);

    private final Path root;
    private final AudioSystem audioSystem;
    private final RecordingFileResolver<TInfo> resolver;
    private final RecursiveDirectoryWatcher watcher;

    private final FileBasedRecordingItemFactory<TInfo, TRecording> itemFactory;

    public FileBasedRecordingDatabase(Path root, AudioSystem audioSystem,
                                      RecordingFileResolver<TInfo> resolver,
                                      FileBasedRecordingItemFactory<TInfo, TRecording> factory) {
        this.root = root;
        this.audioSystem = audioSystem;
        this.watcher = new RecursiveDirectoryWatcher(root);
        this.itemFactory = factory;
        this.resolver = resolver;

        watcher.startWatching();
        watcher.addOnCreated(x -> refreshRecordings());
        watcher.addOnDeleted(x -> refreshRecordings());
        refreshRecordings();
    }

    public static <TInfo> FileBasedRecordingItemFactory<TInfo, FileBasedRecordingItem<TInfo>> defaultFactory() {
        return FileBasedRecordingItem::new;
    }

    @Override
    public ObservableList<TRecording> getAll() {
        return recordingsReadOnly;
    }

    @Override
    public CompletableFuture<Void> remove(RecordingItem<TInfo> name) {
        TInfo info = name.getInfo();
        TRecording recording = lookup.get(info);

        if(recording == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            // Not doing inspection because following through is actually a compile error (might be a bug actually)
            // noinspection Convert2MethodRef
            return CompletableFuture.runAsync(() -> recording.delete());
        }
    }

    private void refreshRecordings() {
        Runnable run = () -> {
            // Remove invalids
            Iterator<TRecording> iter = recordings.iterator();
            while (iter.hasNext()) {
                FileBasedRecordingItem<TInfo> name = iter.next();
                if (!name.isValid()) {
                    iter.remove();
                    lookup.remove(name.getInfo());
                }
            }

            // Add new ones, doesn't do anything if the name is already in the system
            resolver.getAll(root).forEach(this::internalAdd);
        };

        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    @Override
    public CompletableFuture<TRecording> createNew(TInfo info, AudioClip recording) {
        if (lookup.containsKey(info)) {
            throw new RuntimeException("Name with this info already exists");
        }

        return audioSystem.saveAudio(recording, resolver.getPathFor(root, info))
                .thenApply(ignore -> internalAdd(info));
    }

    /**
     * Update/add a name if it does not exist already, otherwise return the existing name
     */
    private TRecording internalAdd(TInfo info) {
        if (lookup.containsKey(info)) {
            return lookup.get(info);
        }

        TRecording item = itemFactory.getFileBasedRecordingItem(info, root, resolver, audioSystem);
        recordings.add(item);
        lookup.put(info, item);
        return item;
    }
}
