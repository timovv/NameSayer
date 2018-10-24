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

/**
 * An AudioClip that is backed by an AudioData object in memory.
 * The audio data is piped from memory to FFplay when the audio is played.
 */
public class MemoryBackedAudioClip implements AudioClip {

    private final AudioData data;
    private final Consumer<Process> onProcessCreated;

    /**
     * Create a new MemoryBackedAudioClip with the given data.
     *
     * @param data             the audio data to be wrapped by this audio clip.
     * @param onProcessCreated A callback for when the ffplay process is created, used by the FFmpegAudioSystem to ensure
     *                         that only one FFplay instance is running at a time. Must not be null.
     */
    MemoryBackedAudioClip(AudioData data, Consumer<Process> onProcessCreated) {
        this.data = Objects.requireNonNull(data);
        this.onProcessCreated = Objects.requireNonNull(onProcessCreated);
    }

    /**
     * Use ffplay to play this audio clip.
     * {@inheritDoc}
     */
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
                // closing the channel will cause ffmpeg to exit, as we need.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<AudioData> getAudioData() {
        return CompletableFuture.completedFuture(data);
    }
}
