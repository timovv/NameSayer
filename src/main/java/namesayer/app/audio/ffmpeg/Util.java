package namesayer.app.audio.ffmpeg;

import namesayer.app.audio.AudioData;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    static Process runResourceScript(String resourceName, String... command) throws IOException {
        List<String> commandList = new ArrayList<>();

        Path location;
        try {
            location = new File(Util.class.getResource(resourceName).toURI()).toPath();
        } catch(URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        commandList.add("bash");
        commandList.add(location.toString());
        commandList.addAll(Arrays.asList(command));

        ProcessBuilder pb = new ProcessBuilder(commandList);
        return pb.start();
    }
}
