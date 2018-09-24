package namesayer.app.database;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Bean containing relevant metadata about a name.
 */
public final class NameInfo {
    private final String name;
    private final String creator;
    private final LocalDateTime creationDate;

    public NameInfo(String name, String creator, LocalDateTime creationDate) {
        this.name = name;
        this.creator = creator;

        // truncate this to seconds since that's all we want to be accurate to
        this.creationDate = creationDate.truncatedTo(ChronoUnit.SECONDS);
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public LocalDateTime getCreationTime() {
        return creationDate;
    }

    @Override
    public boolean equals(Object other) {
        NameInfo otherNameInfo = (NameInfo) other;
        return otherNameInfo.name.equals(this.name) && otherNameInfo.creationDate.equals(creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, creator, creationDate);
    }
}
