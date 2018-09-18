package namesayer.app.database;

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
}
