package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An audio clip that is backed by a file on the file system.
 * This audio clip will be played by ffplay when play() is called.
 */
public class FileBackedAudioClip implements AudioClip {

    private final Path location;
    private final Consumer<Process> onProcessCreated;
    private AudioData data = null;

    /**
     * Create a new FileBackedAudioClip corresponding to the given path.
     *
     * @param location         The path corresponding to the file's location, must not be null.
     * @param onProcessCreated A callback for when the ffplay process is created, used by the FFmpegAudioSystem to ensure
     *                         that only one FFplay instance is running at a time. Must not be null.
     */
    public FileBackedAudioClip(Path location, Consumer<Process> onProcessCreated) {
        this.location = Objects.requireNonNull(location);
        this.onProcessCreated = Objects.requireNonNull(onProcessCreated);
    }

    /**
     * Use ffplay to play this audio clip.
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> play() {
        return CompletableFuture.runAsync(() -> {

            int exitCode;
            try {
                Process proc = Util.runResourceScript("/scripts/play_file.sh", location.toString());
                onProcessCreated.accept(proc);
                exitCode = proc.waitFor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                return;
            }

            if (exitCode != 0) {
                throw new RuntimeException("ffplay exited with exit code: " + exitCode);
            }
        });
    }

    /**
     * This method is not currently supported for file backed audio clips.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public CompletableFuture<AudioData> getAudioData() {
        // TODO timo 2018-09-17 - this isn't critical for assignment 3 but could be important in future if we
        // TODO timo 2018-09-17 - do audio visualisations etc for existing files.
        throw new UnsupportedOperationException("Not supported");
    }
}
