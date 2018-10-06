package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileBasedName extends FileBasedRecordingItem<NameInfo> implements Name {

    private static final Path BAD_QUALITY_FILE = Paths.get("bad_quality.txt");

    private boolean isBadQuality;

    public FileBasedName(NameInfo nameInfo, Path basePath, RecordingFileResolver<NameInfo> resolver, AudioSystem audioSystem) {
        super(nameInfo, basePath, resolver, audioSystem);
        setUpBadQuality();
    }

    @Override
    public boolean isBadQuality() {
        return isBadQuality;
    }


    public void setBadQuality(boolean value) {
        if (value == isBadQuality) {
            return;
        }

        isBadQuality = value;

        Set<String> badQualityFiles;
        try {
            badQualityFiles = new HashSet<>(Files.readAllLines(BAD_QUALITY_FILE));
        } catch (IOException e) {
            throw new NameSayerException("Could not update bad quality register", e);
        }

        if (isBadQuality) {
            badQualityFiles.add(getPathOfThisItem().getFileName().toString());
        } else {
            badQualityFiles.remove(getPathOfThisItem().getFileName().toString());
        }

        try {
            Files.write(BAD_QUALITY_FILE, badQualityFiles);
        } catch (IOException e) {
            throw new NameSayerException("Could not update bad quality register", e);
        }
    }

    /**
     * Figure out whether this recording was previously marked as bad quality or not
     */
    private void setUpBadQuality() {
        if (!Files.exists(BAD_QUALITY_FILE)) {
            try {
                Files.createFile(BAD_QUALITY_FILE);
            } catch (IOException e) {
                throw new NameSayerException("Could not set up bad quality file", e);
            }
        }

        String entry = getPathOfThisItem().getFileName().toString();

        try {
            isBadQuality = Files.readAllLines(BAD_QUALITY_FILE).contains(entry);
        } catch (IOException e) {
            isBadQuality = false;
        }
    }
}
