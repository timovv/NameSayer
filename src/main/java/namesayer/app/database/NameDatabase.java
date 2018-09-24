package namesayer.app.database;

import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * A database of names.
 */
public interface NameDatabase {

    /**
     * @return a collection of all names in the database, which is updated live.
     */
    ObservableList<Name> getNames();

    /**
     * Add the given name to the database with the given parameters.
     */
    default CompletableFuture<Name> addName(String name, String creator, AudioClip recording) {
        return addName(new NameInfo(name, creator, LocalDateTime.now()), recording);
    }

    /**
     * Remove the given name from the database.
     */
    CompletableFuture<Void> removeName(Name name);

    /**
     * Add the given name to the database.
     */
    CompletableFuture<Name> addName(NameInfo nameInfo, AudioClip recording);
}
