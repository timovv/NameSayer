package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;
import namesayer.app.audio.AudioSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FFmpegAudioSystem implements AudioSystem {

    private final Map<Path, FileBackedAudioClip> fileBackedAudioClips = new HashMap<>();

    @Override
    public CompletableFuture<AudioClip> recordAudio(Duration duration) {
        return CompletableFuture.supplyAsync(() -> {

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-f", "pulse",
                    "-i", "default", // TODO timo 2018-09-17 - let the user choose their audio source
                    "-t", Long.toString(duration.getSeconds()), // BUG timo 2018-09-17 - duration is truncated
                    "-f", Util.getFormatArg(true, 16, false),
                    "-ar", "44100",
                    "-ac", "1", // mono
                    "pipe:"
            ).redirectError(ProcessBuilder.Redirect.INHERIT);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Process process;
            try {
                process = pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] buf = new byte[1024];
            int count;
            while (process.isAlive()) {
                try {
                    count = process.getInputStream().read(buf);
                } catch (IOException e) {
                    break;
                }

                if (count > 0) {
                    os.write(buf, 0, count);
                }
            }

            return new MemoryBackedAudioClip(new AudioData(44100,
                    16,
                    true,
                    false,
                    ByteBuffer.wrap(os.toByteArray())));
        });
    }

    @Override
    public CompletableFuture<AudioClip> loadAudio(Path location) {
        return CompletableFuture.completedFuture(
                fileBackedAudioClips.computeIfAbsent(location, FileBackedAudioClip::new)
        );
    }

    @Override
    public CompletableFuture<Void> saveAudio(AudioClip recording, Path location) {
        return recording.getAudioData()
                .thenAccept(data -> {
                    ProcessBuilder pb = new ProcessBuilder(
                            "ffmpeg",
                            "-y",
                            "-f", Util.getFormatArg(data),
                            "-ac", "1",
                            "-i", "pipe:",
                            location.toString()
                    ).redirectError(ProcessBuilder.Redirect.INHERIT);

                    Process process;
                    try {
                        process = pb.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    WritableByteChannel channel = Channels.newChannel(process.getOutputStream());
                    ByteBuffer toWrite = data.getData();
                    try {
                        while(toWrite.hasRemaining()) {
                            channel.write(toWrite);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        //
                    }
                });
    }
}
