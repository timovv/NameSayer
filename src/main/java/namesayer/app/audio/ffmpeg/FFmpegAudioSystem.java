package namesayer.app.audio.ffmpeg;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FFmpegAudioSystem implements AudioSystem {

    // cache of audio clips
    private final Map<Path, FileBackedAudioClip> fileBackedAudioClips = new HashMap<>();

    private final AudioRecorder audioRecorder = new AudioRecorder();

    public FFmpegAudioSystem() {
    }

    @Override
    public double getInputLevel() {
        return audioRecorder.getInputLevel();
    }

    @Override
    public void startRecording() {
        audioRecorder.start();
    }

    @Override
    public AudioClip stopRecording() {
        return audioRecorder.stop();
    }

    @Override
    public boolean isRecording() {
        return audioRecorder.isRecording();
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
                // save audio to file using ffmpeg, ffmpeg takes data from stdin
                .thenAccept(data -> {
                    ProcessBuilder pb = new ProcessBuilder(
                            "ffmpeg",
                            "-y",
                            "-f", Util.getFormatArg(data),
                            "-ac", "1",
                            "-i", "pipe:",
                            location.toString()
                    );

                    Process process;
                    try {
                        process = pb.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // write the bytes to ffmpeg
                    WritableByteChannel channel = Channels.newChannel(process.getOutputStream());
                    ByteBuffer toWrite = data.getData();
                    try {
                        while(toWrite.hasRemaining()) {
                            channel.write(toWrite);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        // this tells ffmpeg to stop
                        process.getOutputStream().close();
                    } catch(IOException e) {
                        throw new NameSayerException("Could not close the ffmpeg stream", e);
                    }

                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        //
                    }
                });
    }
}
