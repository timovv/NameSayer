package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class NameMock implements Name {

    public NameMock() {

    }

    @Override
    public NameInfo getNameInfo() {
        return null;
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        return null;
    }

    @Override
    public boolean isBadQuality() {
        return false;
    }

    @Override
    public void setBadQuality(boolean value) {

    }

    @Override
    public ObservableList<Attempt> getAttempts() {
        return null;
    }

    @Override
    public CompletableFuture<Void> addAttempt(AudioClip recording, LocalDateTime creationTime) {
        return null;
    }

    @Override
    public CompletableFuture<Void> removeAttempt(Attempt toRemove) {
        return null;
    }
}
