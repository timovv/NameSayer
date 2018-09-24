package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FileBackedAudioClip implements AudioClip {

    private final Path location;
    private AudioData data = null;

    public FileBackedAudioClip(Path location) {
        this.location = location;
    }

    @Override
    public CompletableFuture<Void> play() {
        return CompletableFuture.runAsync(() -> {
            // play from the file
            ProcessBuilder pb = new ProcessBuilder(
                    "ffplay",
                    "-nodisp",
                    "-autoexit",
                    location.toString()
            );

            int exitCode;
            try {
                exitCode = pb.start().waitFor();
            } catch(IOException e) {
                throw new RuntimeException(e);
            } catch(InterruptedException e) {
                return;
            }

            if(exitCode != 0) {
                throw new RuntimeException("ffplay exited with exit code: " + exitCode);
            }
        });
    }

    @Override
    public CompletableFuture<AudioData> getAudioData() {
        if(data != null) {
            return CompletableFuture.completedFuture(data);
        } else {
            return CompletableFuture.<AudioData>supplyAsync(() -> {
                // TODO timo 2018-09-17 - this isn't critical for assignment 3 but could be important in future if we
                // TODO timo 2018-09-17 - do audio visualisations etc for existing files.
                throw new UnsupportedOperationException("Not supported");
            }).thenApply(x -> {
                this.data = x;
                return x;
            });
        }
    }
}
