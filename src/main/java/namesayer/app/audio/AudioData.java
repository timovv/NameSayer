package namesayer.app.audio;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// fixme: does not support multi-channel audio at the moment so we are just doing everything in mono, i guess thats ok
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

    public int getSampleRate() {
        return sampleRate;
    }

    public int getSampleResolutionBits() {
        return sampleResolutionBits;
    }

    public boolean isBigEndian() {
        return isBigEndian;
    }

    public ByteBuffer getData() {
        return data.asReadOnlyBuffer();
    }

    public boolean isSigned() {
        return isSigned;
    }

    public Duration getDuration() {
        return Duration.ofNanos((data.remaining() / getSampleResolutionBits() / 8
                * TimeUnit.SECONDS.toNanos(1)) / getSampleRate());
    }
}
