package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface NameDatabase {
    ObservableList<Name> getNames();

    default CompletableFuture<Name> addName(String name, String creator, AudioClip recording) {
        return addName(new NameInfo(name, creator, LocalDateTime.now()), recording);
    }

    CompletableFuture<Void> removeName(Name name);

    CompletableFuture<Name> addName(NameInfo nameInfo, AudioClip recording);
}
