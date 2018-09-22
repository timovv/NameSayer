package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;


public interface Name {

    default String getName() {
        return getNameInfo().getName();
    }

    default LocalDateTime getCreationDate() {
        return getNameInfo().getCreationTime();
    }

    NameInfo getNameInfo();

    CompletableFuture<AudioClip> getRecording();

    boolean isBadQuality();

    void setBadQuality(boolean value);

    ObservableList<Attempt> getAttempts();

    CompletableFuture<Void> addAttempt(AudioClip recording, LocalDateTime creationTime);

    CompletableFuture<Void> removeAttempt(Attempt toRemove);
}
