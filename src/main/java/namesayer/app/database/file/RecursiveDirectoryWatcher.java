package namesayer.app.database.file;


import namesayer.app.NameSayerException;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Watches a directory and all its subdirectories for addition and deletion
 * of files and update listeners when changes occur.
 * <p>
 * The watcher works on a background thread, waiting for changes to occur; after object
 * creation, the watcher must be activated using the startWatching() method.
 */
public class RecursiveDirectoryWatcher implements AutoCloseable {

    private final WatchService service;

    private Thread worker;

    private final Set<Consumer<Path>> onCreatedListeners = new HashSet<>();
    private final Set<Consumer<Path>> onDeletedListeners = new HashSet<>();

    /**
     * Create a new RecursiveDirectoryWatcher.
     *
     * @param path The path of the directory to watch. Must be a directory.
     */
    public RecursiveDirectoryWatcher(Path path) {
        try {
            service = path.getFileSystem().newWatchService();
        } catch (IOException e) {
            throw new NameSayerException("Could not create watch service for " + path, e);
        }

        if (!Files.isDirectory(path)) {
            throw new NameSayerException("path " + path + " must be a directory");
        }

        try {
            Files.walk(path).filter(Files::isDirectory).forEach(p -> {
                try {
                    p.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
                } catch (IOException e) {
                    throw new NameSayerException("Could not start watch for directory " + p, e);
                }
            });
        } catch (IOException e) {
            throw new NameSayerException("Could not search for subdirectories of " + path, e);
        }

    }

    /**
     * Start watching the directory.
     */
    public void startWatching() {
        worker = new Thread(this::work);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * Stop watching the directory.
     */
    public void stopWatching() {
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    /**
     * @return true if the watcher is current recording the directory.
     */
    public boolean isWatching() {
        return worker != null;
    }

    /**
     * Worker thread for directory watching.
     */
    @SuppressWarnings("unchecked")
    private void work() {
        for (; ; ) {
            WatchKey key;
            try {
                key = service.take();
            } catch (InterruptedException e) {
                break;
            }

            if (key == null) {
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind().equals(StandardWatchEventKinds.OVERFLOW)) {
                    continue;
                }

                // Weird 'feature' of WatchService: the path from the event is relative - not to the pwd,
                // but to the DIRECTORY BEING WATCHED.
                // so calling toAbsolutePath DOES NOT GIVE THE CORRECT PATH. Kind of misleading, but by design apparently.

                Path outputPath = ((WatchEvent<Path>) event).context();
                // Append the output path to what we're watching.
                Path actualPath = ((Path) key.watchable()).resolve(outputPath);
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    if (Files.isDirectory(actualPath)) {
                        try {
                            actualPath.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
                        } catch (IOException e) {
                            // todo handle
                        }
                    }

                    onCreatedListeners.forEach(x -> x.accept(actualPath));
                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    onDeletedListeners.forEach(x -> x.accept(actualPath));
                }
            }

            key.reset();
        }
    }

    /**
     * Add a listener for when a file is created.
     *
     * @param listener A callback which is called on the background thread when a file is created. The callback
     *                 should not perform long-running work, as otherwise the watcher may miss changes.
     */
    public void addOnCreated(Consumer<Path> listener) {
        onCreatedListeners.add(Objects.requireNonNull(listener));
    }

    /**
     * Add a listener for when a file is deleted.
     *
     * @param listener A callback which is called on the background thread when a file is deleted. The callback
     *                 should not perform long-running work, as otherwise the watcher may miss changes.
     */
    public void addOnDeleted(Consumer<Path> listener) {
        onDeletedListeners.add(Objects.requireNonNull(listener));
    }

    /**
     * Unsubscribe the given listener from directory creation events.
     */
    public void removeOnCreated(Consumer<Path> listener) {
        onCreatedListeners.remove(Objects.requireNonNull(listener));
    }

    /**
     * Unsubscribe the given listener from directory deletion events.
     */
    public void removeOnDeleted(Consumer<Path> listener) {
        onCreatedListeners.remove(Objects.requireNonNull(listener));
    }

    /**
     * Stops watching the directory if it was being watched.
     */
    @Override
    public void close() {
        if (isWatching()) {
            stopWatching();
        }
    }
}