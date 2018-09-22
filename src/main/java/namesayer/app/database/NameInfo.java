package namesayer.app.database;

import java.time.LocalDateTime;

public final class NameInfo {
    private final String name;
    private final String creator;
    private final LocalDateTime creationDate;

    public NameInfo(String name, String creator, LocalDateTime creationDate) {
        this.name = name;
        this.creator = creator;
        this.creationDate = creationDate;
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
        NameInfo otherNameInfo = (NameInfo)other;
        return otherNameInfo.name.equals(this.name) && otherNameInfo.creationDate.equals(creationDate);
    }
}
