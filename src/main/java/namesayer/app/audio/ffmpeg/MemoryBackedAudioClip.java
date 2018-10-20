package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MemoryBackedAudioClip implements AudioClip {

    private final AudioData data;
    private final Consumer<Process> onProcessCreated;

    MemoryBackedAudioClip(AudioData data, Consumer<Process> onProcessCreated) {
        this.data = Objects.requireNonNull(data);
        this.onProcessCreated = Objects.requireNonNull(onProcessCreated);
    }

    @Override
    public CompletableFuture<Void> play() {
        return CompletableFuture.runAsync(() -> {
            // ffplay will play from stdin, which we will give it
            ProcessBuilder pb = new ProcessBuilder("ffplay",
                    "-autoexit",
                    "-nodisp",
                    "-f", Util.getFormatArg(data),
                    "-ac", "1",
                    "pipe:"
            );

            Process process;

            try {
                process = pb.start();
                onProcessCreated.accept(process);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            WritableByteChannel channel = Channels.newChannel(process.getOutputStream());
            ByteBuffer toWrite = data.getData();
            try {
                while (toWrite.hasRemaining()) {
                    channel.write(toWrite);
                }
                channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int exit;
            try {
                exit = process.waitFor();
            } catch (InterruptedException e) {
                return;
            }

            if (exit != 0) {
                throw new RuntimeException("ffplay exited with exit code: " + exit);
            }
        });
    }

    @Override
    public CompletableFuture<AudioData> getAudioData() {
        return CompletableFuture.completedFuture(data);
    }
}
