package namesayer.app.audio.ffmpeg;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class AudioRecorder {

    private static int SAMPLE_FREQ_HZ = 44100;
    private static int SAMPLE_RES_BITS = 16;
    private static boolean SAMPLE_SIGNED = true;
    private static boolean SAMPLE_BIG_ENDIAN = false;

    private Process ffmpegProcess;
    private final Object streamLock = new Object();
    ByteArrayOutputStream stream;

    private final Thread recordingThread;
    private final Object micLevelLock = new Object();
    private final byte[] micLevelBuffer;
    private int micLevelIndex = 0;
    private double micLevel = 0;

    public AudioRecorder() {
        // store 400ms worth of audio in our mic level buffer
        micLevelBuffer = new byte[SAMPLE_FREQ_HZ * (SAMPLE_RES_BITS / 8) * 4 / 10];

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-f", "pulse",
                "-i", "default", // TODO timo 2018-09-17 - let the user choose their audio source
                "-f", Util.getFormatArg(SAMPLE_SIGNED, SAMPLE_RES_BITS, SAMPLE_BIG_ENDIAN),
                "-ar", Integer.toString(SAMPLE_FREQ_HZ),
                "-ac", "1", // mono
                "pipe:"
        );

        try {
            ffmpegProcess = pb.start();
        } catch (IOException e) {
            throw new NameSayerException("Could not start ffmpeg for recoridng");
        }

        recordingThread = new Thread(this::recordingThread, "ffmpeg recording thread");
        recordingThread.setDaemon(true);
        recordingThread.start();
    }

    public void start() {
        synchronized (streamLock) {
            if (stream != null) {
                throw new IllegalStateException("Cannot start recording if recording already");
            }

            stream = new ByteArrayOutputStream();
        }
    }

    public AudioClip stop() {
        byte[] data;

        synchronized (streamLock) {
            if(stream == null) {
                throw new IllegalStateException("Cannot stop recording if not recording already");
            }

            data = stream.toByteArray();
            stream = null;
        }

        return new MemoryBackedAudioClip(
                new AudioData(SAMPLE_FREQ_HZ, SAMPLE_RES_BITS,
                        true, false,
                        ByteBuffer.wrap(data)));
    }

    public double getInputLevel() {
        ByteBuffer data = ByteBuffer.allocate(micLevelBuffer.length);
        synchronized (micLevelLock) {
            data.put(micLevelBuffer);
        }

        data.flip();

        // note: this code assumes 16 bit sample frequency, which is set in the constants at the top of the file.
        // so if the constants change, this code needs to change as well.

        double rms = 0.;
        while(data.hasRemaining()) {
            short next = data.getShort(); // 16 bits
            rms += ((int)(next) * next) / (micLevelBuffer.length / 2);
        }

        return rms;

        // log(root(mean-square)) = 1/2 log(mean-square)
//        return Math.log10(rms) / 2;
    }

    public boolean isRecording() {
        return stream != null;
    }

    int debugcount = 0;
    private void recordingThread() {
        byte[] buf = new byte[1024];
        int count;

        while (ffmpegProcess.isAlive()) {
            try {
                count = ffmpegProcess.getInputStream().read(buf);
            } catch (IOException e) {
                break;
            }

            if (count > 0) {
                synchronized (streamLock) {
                    if (stream != null) {
                        stream.write(buf, 0, count);
                    }
                }

                synchronized (micLevelLock) {
                    System.out.println(debugcount++);
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
