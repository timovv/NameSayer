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

/**
 * An audio system that is powered by FFmpeg.
 * <p>
 * For this audio system to work, ffmpeg and ffplay must both be installed and on the users' PATH.
 */
public class FFmpegAudioSystem implements AudioSystem {

    // cache of audio clips

    /**
     * This cache ensures that there is only one FileBackedAudioClip stored for any one file in the database.
     */
    private final Map<Path, FileBackedAudioClip> fileBackedAudioClips = new HashMap<>();

    /**
     * The AudioRecorder instance associated with this audio system.
     */
    private final AudioRecorder audioRecorder = new AudioRecorder(this::handleNewFFPlayProcess);

    /**
     * The current FFplay process (if any)
     */
    private volatile Process ffplayProcess;

    /**
     * Create a new FFmpegAudioSystem.
     */
    public FFmpegAudioSystem() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getInputLevel() {
        return audioRecorder.getInputLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecording() {
        audioRecorder.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioClip stopRecording() {
        return audioRecorder.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecording() {
        return audioRecorder.isRecording();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<AudioClip> loadAudio(Path location) {
        return CompletableFuture.completedFuture(
                fileBackedAudioClips.computeIfAbsent(location,
                        p -> new FileBackedAudioClip(p, this::handleNewFFPlayProcess))
        );
    }

    /**
     * Handler for when a new FFplay process is created. This handler keeps track of the previous FFplay process and
     * kills the new one if sound is still being played, to ensure that only one audio clip is played at once.
     */
    private void handleNewFFPlayProcess(Process proc) {
        if (ffplayProcess != null && ffplayProcess.isAlive()) {
            proc.destroyForcibly();
            return;
        }

        ffplayProcess = proc;
    }

    /**
     * Save the audio to the given location using ffmpeg.
     * {@inheritDoc}
     */
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
                        while (toWrite.hasRemaining()) {
                            channel.write(toWrite);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        // this tells ffmpeg to stop
                        process.getOutputStream().close();
                    } catch (IOException e) {
                        throw new NameSayerException("Could not close the ffmpeg stream", e);
                    }

                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        // this is okay
                    }
                });
    }
}
