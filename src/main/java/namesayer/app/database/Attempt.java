package namesayer.app.database;

import java.time.LocalDateTime;
import java.util.List;

public interface Attempt extends RecordingItem<AttemptInfo> {

    default LocalDateTime getAttemptTime() {
        return getInfo().getAttemptTime();
    }

    default List<String> getNames() {
        return getInfo().getNames();
    }
}
