package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioData;

final class Util {

    /**
     * Given an AudioData object, return an argument for ffmpeg for that kind of data using the pcm encoding.
     */
    static String getFormatArg(AudioData data) {
        return getFormatArg(data.isSigned(), data.getSampleResolutionBits(), data.isBigEndian());
    }

    /**
     * Generate an argument for ffmpeg's format field using the pcm encoding given the parameters.
     */
    static String getFormatArg(boolean signed, int sampleResolutionBits, boolean isBigEndian) {
        return (signed ? "s" : "u")
                + sampleResolutionBits
                + (isBigEndian ? "be" : "le");
    }
}
