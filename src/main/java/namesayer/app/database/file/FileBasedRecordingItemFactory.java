package namesayer.app.database.file;

import namesayer.app.audio.AudioSystem;

import java.nio.file.Path;

/**
 * welcome to generics hell :)
 * @param <TInfo>
 * @param <TRecording>
 */
@FunctionalInterface
public interface FileBasedRecordingItemFactory<TInfo, TRecording extends FileBasedRecordingItem<TInfo>> {
    TRecording getFileBasedRecordingItem(TInfo info, Path basePath, RecordingFileResolver<TInfo> resolver, AudioSystem audioSystem);
}
