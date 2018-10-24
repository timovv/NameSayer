package namesayer.app.database.file;

import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;
import namesayer.app.database.AttemptInfo;
import namesayer.app.database.Name;
import namesayer.app.database.NameInfo;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.database.RecordingDatabase;

import java.nio.file.Path;

/**
 * A NameSayerDatabase that is based on a flat file.
 */
public class FileBasedNameSayerDatabase implements NameSayerDatabase {

    private final FileBasedRecordingDatabase<NameInfo, ? extends Name> nameDatabase;
    private final FileBasedRecordingDatabase<AttemptInfo, ? extends Attempt> attemptDatabase;

    public FileBasedNameSayerDatabase(Path namesRoot, Path attemptsRoot, AudioSystem audio) {
        this.nameDatabase = new FileBasedRecordingDatabase<>(namesRoot, audio, new SE206NameFileResolver(), FileBasedName::new);
        this.attemptDatabase = new FileBasedRecordingDatabase<>(attemptsRoot, audio, new SE206AttemptFileResolver(), FileBasedAttempt::new);
    }

    @Override
    public RecordingDatabase<NameInfo, ? extends Name> getNameDatabase() {
        return nameDatabase;
    }

    @Override
    public RecordingDatabase<AttemptInfo, ? extends Attempt> getAttemptDatabase() {
        return attemptDatabase;
    }
}
