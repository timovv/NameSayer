package namesayer.app.database;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class AttemptInfo {

    private final List<String> names;
    private final LocalDateTime attemptTime;

    public AttemptInfo(List<String> names, LocalDateTime attemptTime) {
        this.names = names;
        this.attemptTime = attemptTime.truncatedTo(ChronoUnit.SECONDS);
    }

    public List<String> getNames() {
        return names;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(names, attemptTime);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AttemptInfo)) {
            return false;
        }

        AttemptInfo other = (AttemptInfo)obj;
        return this.names.equals(other.names) && attemptTime.equals(other.attemptTime);
    }
}
