package namesayer.app.database;

import java.time.LocalDateTime;
import java.util.List;

public class AttemptInfo {

    private final List<String> names;
    private final LocalDateTime attemptTime;

    public AttemptInfo(List<String> names, LocalDateTime attemptTime) {
        this.names = names;
        this.attemptTime = attemptTime;
    }

    public List<String> getNames() {
        return names;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }
}
