package namesayer.app.database;

public interface NameSayerDatabase {

    RecordingDatabase<NameInfo, ? extends Name> getNameDatabase();

    RecordingDatabase<AttemptInfo, ? extends Attempt> getAttemptDatabase();
}
