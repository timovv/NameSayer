package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface NameDatabase {
    ObservableList<Name> getNames();

    default CompletableFuture<Name> addName(String name, AudioClip recording) {
        return addName(new NameInfo(name, LocalDateTime.now()), recording);
    }

    CompletableFuture<Name> addName(NameInfo nameInfo, AudioClip recording);
}
