package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.RecordingItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * A recording item that is associated with a file on disk.'
 *
 * @param <TInfo>
 */
public class FileBasedRecordingItem<TInfo> implements RecordingItem<TInfo> {

    private final TInfo info;
    private final Path basePath;
    private final Path pathOfThisItem;
    private final RecordingFileResolver<TInfo> resolver;
    private final AudioSystem audioSystem;

    /**
     * Create a new FileBasedRecordingItem with the given parameters.
     *
     * @param nameInfo    The metadata behind this recording item.
     * @param basePath    The base path of the database this recording item is in.
     * @param resolver    The resolver used by the database this recording item is in.
     * @param audioSystem The audio system used by the database; used to load the audio from this recording for playing.
     * @throws NameSayerException if no recording exists corresponding to the input info object.
     */
    public FileBasedRecordingItem(TInfo nameInfo, Path basePath, RecordingFileResolver<TInfo> resolver, AudioSystem audioSystem) {
        this.resolver = resolver;
        this.audioSystem = audioSystem;
        this.info = nameInfo;
        this.basePath = basePath;
        this.pathOfThisItem = resolver.getPathFor(basePath, nameInfo);

        if (!isValid()) {
            throw new NameSayerException("Not a valid path for this resolver");
        }
    }

    /**
     * @return The info associated with this recording item.
     */
    @Override
    public TInfo getInfo() {
        return info;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NameSayerException if this recording item has become invalid.
     */
    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        return getAudioSystem().loadAudio(getPathOfThisItem());
    }

    /**
     * Determines whether this recording is valid, that is, determines if the file associated with this recording exists
     * and if the path associated is valid.
     *
     * @return true if the recording is valid, false otherwise.
     */
    boolean isValid() {
        return Files.exists(getPathOfThisItem()) && getResolver().getInfo(getPathOfThisItem()).isPresent();
    }

    /**
     * Delete this name from the file system (will invalidate)
     */
    void delete() {
        try {
            // the file deletion will trigger an update of the database.
            Files.delete(getPathOfThisItem());
        } catch (IOException e) {
            throw new NameSayerException("Could not delete recording", e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FileBasedRecordingItem)) {
            return false;
        }

        return this.getInfo().equals(((FileBasedRecordingItem<?>) other).getInfo());
    }

    /**
     * @return the base path of the database this item is stored in
     */
    protected Path getBasePath() {
        return basePath;
    }

    /**
     * @return the path of the file associated with this recording item
     */
    protected Path getPathOfThisItem() {
        return pathOfThisItem;
    }

    /**
     * @return the resolver associated with the database this recording item is in
     */
    protected RecordingFileResolver<TInfo> getResolver() {
        return resolver;
    }

    /**
     * @return the audio system associated with the database this recording item is in.
     */
    protected AudioSystem getAudioSystem() {
        return audioSystem;
    }
}
