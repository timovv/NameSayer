package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioData;

final class Util {
    static String getFormatArg(AudioData data) {
        return getFormatArg(data.isSigned(), data.getSampleResolutionBits(), data.isBigEndian());
    }

    static String getFormatArg(boolean signed, int sampleResolutionBits, boolean isBigEndian) {
        return (signed ? "s" : "u")
                + sampleResolutionBits
                + (isBigEndian ? "be" : "le");
    }
}
