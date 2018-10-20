package namesayer.app.database.file;

import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;
import namesayer.app.database.AttemptInfo;

import java.nio.file.Path;

/**
 * An attempt (practice recording) is a 'recording item' which can be recorded in the practice menu.
 * This FileBasedAttempt class represents an attempt which is to be stored in a file on the file system.
 */
public class FileBasedAttempt extends FileBasedRecordingItem<AttemptInfo> implements Attempt {
    public FileBasedAttempt(AttemptInfo nameInfo, Path basePath, RecordingFileResolver<AttemptInfo> resolver, AudioSystem audioSystem) {
        super(nameInfo, basePath, resolver, audioSystem);
    }
}
