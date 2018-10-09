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
 * A name that is based on a flat file
 */
public class FileBasedRecordingItem<TInfo> implements RecordingItem<TInfo> {

    private final TInfo info;
    private final Path basePath;
    private final Path pathOfThisItem;
    private final RecordingFileResolver<TInfo> resolver;
    private final AudioSystem audioSystem;

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

    @Override
    public TInfo getInfo() {
        return info;
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        return getAudioSystem().loadAudio(getPathOfThisItem());
    }



    boolean isValid() {
        return Files.exists(getPathOfThisItem()) && getResolver().getInfo(getPathOfThisItem()).isPresent();
    }

    /**
     * Delete this name (will invalidate)
     */
    void delete() {
        try {
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

    protected Path getBasePath() {
        return basePath;
    }

    protected Path getPathOfThisItem() {
        return pathOfThisItem;
    }

    protected RecordingFileResolver<TInfo> getResolver() {
        return resolver;
    }

    protected AudioSystem getAudioSystem() {
        return audioSystem;
    }
}
