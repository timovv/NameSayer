package namesayer.app.audio.ffmpeg;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * This class is responsible for handling input from the users' microphone, and provides support for detecting the
 * microphone level and also for starting and stopping recording.
 * <p>
 * Only one recording session can happen simultaneously.
 */
class AudioRecorder {

    // IF YOU CHANGE ANY OF THIS, YOU WILL ALSO NEED TO CHANGE, BELOW
    //  - SIZE OF MIC LEVEL BUFFER
    //  - MIC LEVEL CALCULATION
    private static int SAMPLE_FREQ_HZ = 44100;
    private static int SAMPLE_RES_BITS = 16;
    private static boolean SAMPLE_SIGNED = true;
    private static boolean SAMPLE_BIG_ENDIAN = false;
    private final Consumer<Process> ffplayProcessCreationHandler;

    private Process ffmpegProcess;
    private final Object streamLock = new Object();
    ByteArrayOutputStream stream;

    private final Thread recordingThread;
    private final Object micLevelLock = new Object();
    private final byte[] micLevelBuffer;
    private int micLevelIndex = 0;

    /**
     * Create a new AudioRecorder instance.
     *
     * @param ffplayProcessCreationHandler A callback for when an FFplay process is created. This is used by the audio
     *                                     system to ensure that only one FFplay process is being used at any one time.
     */
    public AudioRecorder(Consumer<Process> ffplayProcessCreationHandler) {
        this.ffplayProcessCreationHandler = ffplayProcessCreationHandler;
        // store 100ms worth of audio in our mic level buffer
        micLevelBuffer = new byte[SAMPLE_FREQ_HZ * (SAMPLE_RES_BITS / 8) / 10];

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-f", "pulse",
                "-fragment_size", "1024",
                "-i", "default", // TODO timo 2018-09-17 - let the user choose their audio source
                "-f", Util.getFormatArg(SAMPLE_SIGNED, SAMPLE_RES_BITS, SAMPLE_BIG_ENDIAN),
                "-ar", Integer.toString(SAMPLE_FREQ_HZ),
                "-ac", "1", // mono
                "pipe:"
        );

        // start the recording process.
        try {
            ffmpegProcess = pb.start();
        } catch (IOException e) {
            throw new NameSayerException("Could not start ffmpeg for recoridng");
        }

        recordingThread = new Thread(this::recordingThread, "ffmpeg recording thread");
        recordingThread.setDaemon(true);
        recordingThread.start();
    }

    /**
     * Start recording mic data into a buffer.
     */
    public void start() {
        synchronized (streamLock) {
            if (stream != null) {
                throw new IllegalStateException("Cannot start recording if recording already");
            }

            stream = new ByteArrayOutputStream();
        }
    }

    /**
     * Stop recording microphone data.
     *
     * @return The recorded AudioClip.
     * @throws IllegalStateException if the AudioClip is not currently recording.
     */
    public AudioClip stop() {
        byte[] data;

        synchronized (streamLock) {
            if (stream == null) {
                throw new IllegalStateException("Cannot stop recording if not recording already");
            }

            data = stream.toByteArray();
            stream = null;
        }

        return new MemoryBackedAudioClip(
                new AudioData(SAMPLE_FREQ_HZ, SAMPLE_RES_BITS,
                        true, false,
                        ByteBuffer.wrap(data)), ffplayProcessCreationHandler);
    }

    /**
     * Get the input power of the mic in dB with respect to the maximum value of the audio signal.
     *
     * @return The decibel value of the microphone's current level. This value will always be negative.
     */
    public double getInputLevel() {

        // Put the microphone data into a separate buffer thread safely so that readings can continue safely while this
        // code determines the input level.
        ByteBuffer data = ByteBuffer.allocate(micLevelBuffer.length);
        synchronized (micLevelLock) {
            data.put(micLevelBuffer);
        }

        data.flip();

        // note: this code assumes 16 bit sample frequency and little-endian PCM, which is set in the constants at the top of the file.
        // so if the constants change, this code needs to change as well.

        // This mean square is as a fraction, with 1.0 representing the signal's max value (Short.MAX_VALUE in the case of
        //  16-bit signed PCM)
        double meanSquare = 0.;
        while (data.hasRemaining()) {
            short next = data.getShort(); // 16 bits
            next = (short) (((next & 0xff00) >> 8) | ((next & 0x00ff) << 8)); // java is big endian, our pcm is little endian
            double amplitude = (double) next / Short.MAX_VALUE;
            meanSquare += (amplitude * amplitude) / (micLevelBuffer.length / 2);
        }

        // P = Amplitude^2 ~= Mean-square
        // 10log10(meanSquare) for decibels wrt max possible signal value
        return 10 * Math.log10(meanSquare);
    }

    /**
     * Determine whether this AudioRecorder is currently recording audio.
     *
     * @return true if this AudioRecorder is currently recording audio.
     */
    public boolean isRecording() {
        return stream != null;
    }

    /**
     * This internal method is used to poll the FFmpeg process, reading raw audio data from its standard output.
     * The method will then place the audio into a small buffer to determine the input level, and if recording, also
     * add the data to a ByteArrayOutputStream which will later be used to reconstruct the recorded audio data when
     * recording finishes.
     */
    private void recordingThread() {
        // 1kB buffer seems to work well for our purposes.
        byte[] buf = new byte[1024];
        int count;

        while (ffmpegProcess.isAlive()) {
            // Read data from the FFmpeg input stream
            try {
                count = ffmpegProcess.getInputStream().read(buf);
            } catch (IOException e) {
                break;
            }

            if (count > 0) {
                synchronized (streamLock) {
                    // Write to the output stream if we are recording
                    if (isRecording()) {
                        stream.write(buf, 0, count);
                    }
                }

                // Add to the mic level circular buffer. The last index where data was added to the buffer is
                // kept track of, and data appended after that point. If there is not enough space in the buffer,
                // the data is wrapped around and overwrites the old data at the start of the buffer.
                synchronized (micLevelLock) {
                    if (micLevelIndex + count < micLevelBuffer.length) {
                        System.arraycopy(buf, 0, micLevelBuffer, micLevelIndex, count);
                        micLevelIndex += count;
                    } else {
                        // not enough space in the rest of the array; copy as much as we can and put the rest at the
                        // start
                        System.arraycopy(buf, 0, micLevelBuffer, micLevelIndex, micLevelBuffer.length - micLevelIndex);
                        System.arraycopy(buf, 0, micLevelBuffer, 0, count + micLevelIndex - micLevelBuffer.length);
                        micLevelIndex = count;
                    }
                }
            }
        }
    }
}
