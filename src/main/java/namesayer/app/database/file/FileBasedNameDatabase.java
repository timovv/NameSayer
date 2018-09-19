package namesayer.app.database.file;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameDatabase;
import namesayer.app.database.NameInfo;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class FileBasedNameDatabase implements NameDatabase {

    private final ObservableList<Name> names = FXCollections.observableArrayList();
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

    private void refreshNames() {
        Runnable run = () -> {
            // Remove invalids
            Iterator<Name> iter = names.iterator();
            while(iter.hasNext()) {
                FileBasedName name = (FileBasedName)iter.next();
                if(!name.isValid()) {
                    iter.remove();
                }
            }

            // Add new ones

            resolver.getAllNames(root).forEach(nameInfo -> {
                // if there are none with this path then add it
                if(names.stream().noneMatch(x -> x.getNameInfo().equals(nameInfo))) {
                    names.add(new FileBasedName(nameInfo, root, resolver, audioSystem));
                }
            });
        };

        if(Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    @Override
    public CompletableFuture<Name> addName(NameInfo nameInfo, AudioClip recording) {
        return audioSystem.saveAudio(recording, resolver.getPathForName(root, nameInfo))
                .thenApply(ignore -> {
                    FileBasedName name = new FileBasedName(nameInfo, root, resolver, audioSystem);
                    Platform.runLater(() -> names.add(name));
                    return name;
                });
    }
}
