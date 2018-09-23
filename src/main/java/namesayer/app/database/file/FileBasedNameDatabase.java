package namesayer.app.database.file;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;
import namesayer.app.database.NameInfo;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FileBasedNameDatabase implements NameDatabase {

    private final ObservableList<Name> names = FXCollections.observableArrayList();
    private final Map<NameInfo, Name> lookup = new HashMap<>();
    private final ObservableList<Name> namesReadOnly = FXCollections.unmodifiableObservableList(names);

    private final Path root;
    private final AudioSystem audioSystem;
    private final NameFileResolver resolver = new SE206NameFileResolver();
    private final RecursiveDirectoryWatcher watcher;

    public FileBasedNameDatabase(Path root, AudioSystem audioSystem) {
        this.root = root;
        this.audioSystem = audioSystem;
        this.watcher = new RecursiveDirectoryWatcher(root);

        watcher.startWatching();
        watcher.addOnCreated(x -> refreshNames());
        watcher.addOnDeleted(x -> refreshNames());
        refreshNames();
    }

    @Override
    public ObservableList<Name> getNames() {
        return namesReadOnly;
    }

    @Override
    public CompletableFuture<Void> removeName(Name name) {
        if (!(name instanceof FileBasedName)) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(((FileBasedName) name)::delete);
    }

    private void refreshNames() {
        Runnable run = () -> {
            // Remove invalids
            Iterator<Name> iter = names.iterator();
            while (iter.hasNext()) {
                FileBasedName name = (FileBasedName) iter.next();
                if (!name.isValid()) {
                    iter.remove();
                    lookup.remove(name.getNameInfo());
                }
            }

            // Add new ones, doesn't do anything if the name is already in the system
            resolver.getAllNames(root).forEach(this::internalAddName);

            // update attempts
            for(Name name : getNames()) {
                ((FileBasedName)name).updateAttempts();
            }
        };

        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    @Override
    public CompletableFuture<Name> addName(NameInfo nameInfo, AudioClip recording) {
        if (lookup.containsKey(nameInfo)) {
            throw new RuntimeException("Name with this info already exists");
        }

        return audioSystem.saveAudio(recording, resolver.getPathForName(root, nameInfo))
                .thenApply(ignore -> internalAddName(nameInfo));
    }

    private Name internalAddName(NameInfo nameInfo) {
        if (lookup.containsKey(nameInfo)) {
            return lookup.get(nameInfo);
        }

        FileBasedName name = new FileBasedName(nameInfo, root, resolver, audioSystem);
        names.add(name);
        lookup.put(nameInfo, name);
        return name;
    }
}
