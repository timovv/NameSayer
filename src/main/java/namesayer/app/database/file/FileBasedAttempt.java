package namesayer.app.database.file;

import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;
import namesayer.app.database.AttemptInfo;

import java.nio.file.Path;

public class FileBasedAttempt extends FileBasedRecordingItem<AttemptInfo> implements Attempt {
    public FileBasedAttempt(AttemptInfo nameInfo, Path basePath, RecordingFileResolver<AttemptInfo> resolver, AudioSystem audioSystem) {
        super(nameInfo, basePath, resolver, audioSystem);
    }
}
