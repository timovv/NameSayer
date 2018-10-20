package namesayer.app.audio;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// fixme: does not support multi-channel audio at the moment so we are just doing everything in mono, i guess thats ok

/**
 * A class that represents raw audio data; fields fairly self-explanatory
 */
public final class AudioData {

    private final int sampleRate;
    private final int sampleResolutionBits;
    private final boolean isSigned;
    private final boolean isBigEndian;
    private final ByteBuffer data;

    public AudioData(int sampleRate, int sampleResolutionBits, boolean isSigned, boolean isBigEndian, ByteBuffer data) {
        this.sampleRate = sampleRate;
        this.sampleResolutionBits = sampleResolutionBits;
        this.isSigned = isSigned;
        this.isBigEndian = isBigEndian;
        this.data = data;
    }

    /**
     * @return the sample rate of the raw audio data (in Hz).
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * @return the sample resolution of the raw audio data.
     */
    public int getSampleResolutionBits() {
        return sampleResolutionBits;
    }

    /**
     * @return true if the audio data is big-endian.
     */
    public boolean isBigEndian() {
        return isBigEndian;
    }

    /**
     * @return a read-only buffer representing the raw audio data associated with this AudioClip.
     */
    public ByteBuffer getData() {
        return data.asReadOnlyBuffer();
    }

    /**
     * @return true if the audio data is signed.
     */
    public boolean isSigned() {
        return isSigned;
    }

    /**
     * @return The duration of this AudioClip
     */
    public Duration getDuration() {
        return Duration.ofNanos((data.remaining() / getSampleResolutionBits() / 8
                * TimeUnit.SECONDS.toNanos(1)) / getSampleRate());
    }
}
