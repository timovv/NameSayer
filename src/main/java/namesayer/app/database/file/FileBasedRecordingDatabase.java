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
 * A recording database that is based on a file structure in memory.
 *
 * All recordings are based in a folder on the disk; the structure of the database and the location of files is
 * determined by a {@link RecordingFileResolver} instance.
 *
 * The directory is watched for changes dynamically, with new entries automatically being added to the database.
 *
 * @param <TInfo> The type of the information corresponding to each recording item. This is an object providing metadata
 *               about database entries.
 * @param <TRecording> The type of recording, which must be a {@link FileBasedRecordingItem}.
 */
public class FileBasedRecordingDatabase<TInfo, TRecording extends FileBasedRecordingItem<TInfo>>
        implements RecordingDatabase<TInfo, TRecording> {

    // the master list of recordings
    private final ObservableList<TRecording> recordings = FXCollections.observableArrayList();

    // a map copy of the list for fast lookup
    private final Map<TInfo, TRecording> lookup = new HashMap<>();

    // read only view
    private final ObservableList<TRecording> recordingsReadOnly = FXCollections.unmodifiableObservableList(recordings);

    private final Path root;
    private final AudioSystem audioSystem;
    private final RecordingFileResolver<TInfo> resolver;
    private final RecursiveDirectoryWatcher watcher;

    private final FileBasedRecordingItemFactory<TInfo, TRecording> itemFactory;

    /**
     * Construct a new FileBasedRecordingDatabase with the given parameters.
     * @param root The root of the folder where the recordings are stored.
     * @param audioSystem An audio system, used to load recordings from their files to be played.
     * @param resolver The resolver, which resolves an information object from a given path, and vice-versa.
     * @param factory The factory used to create a RecordingItem given an information object.
     */
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

    /**
     * @return A read-only observable collection of all recordings in the database.
     */
    @Override
    public ObservableList<TRecording> getAll() {
        return recordingsReadOnly;
    }

    /**
     * Asynchronously remove the given RecordingItem from the database, if it is present.
     * @param item The item to remove.
     * @return A CompletableFuture which will return once the recording has been deleted.
     */
    @Override
    public CompletableFuture<Void> remove(RecordingItem<TInfo> item) {
        TInfo info = item.getInfo();
        TRecording recording = lookup.get(info);

        if(recording == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            // Not doing inspection because following through is actually a compile error (might be a bug actually)
            // noinspection Convert2MethodRef
            return CompletableFuture.runAsync(() -> recording.delete());
        }
    }

    /**
     * Refresh recordings from the folder on the JavaFX thread.
     * This refresh is to be triggered when file changes are detected.
     */
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
            resolver.getAll(root).forEach(this::internalAddOrUpdate);
        };

        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<TRecording> createNew(TInfo info, AudioClip recording) {
        if (lookup.containsKey(info)) {
            throw new RuntimeException("Name with this info already exists");
        }

        return audioSystem.saveAudio(recording, resolver.getPathFor(root, info))
                .thenApply(ignore -> internalAddOrUpdate(info));
    }

    /**
     * Creates the recording type if it does not exist and returns it. If the recording already exists, it is returned
     * immediately.
     */
    private TRecording internalAddOrUpdate(TInfo info) {
        if (lookup.containsKey(info)) {
            return lookup.get(info);
        }

        TRecording item = itemFactory.getFileBasedRecordingItem(info, root, resolver, audioSystem);
        recordings.add(item);
        lookup.put(info, item);
        return item;
    }
}
