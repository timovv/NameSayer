package namesayer.app.database.file;

import namesayer.app.audio.AudioSystem;

import java.nio.file.Path;

/**
 * An interface representing a factory that creates recording items based on useful information.
 * @param <TInfo> The type representing the info type of the recording.
 * @param <TRecording> The type representing the recording that this factory should create.
 */
@FunctionalInterface
public interface FileBasedRecordingItemFactory<TInfo, TRecording extends FileBasedRecordingItem<TInfo>> {

    /**
     * Create a new recording object based on the info provided, and other parameters.
     * @param info The info object from which to create the recording object
     * @param basePath The base path of the database in which the recording is stored
     * @param resolver The resolver associated with this database
     * @param audioSystem The audio system associated with this database
     * @return a new recording instance based on the info provided.
     */
    TRecording getFileBasedRecordingItem(TInfo info, Path basePath, RecordingFileResolver<TInfo> resolver, AudioSystem audioSystem);
}
