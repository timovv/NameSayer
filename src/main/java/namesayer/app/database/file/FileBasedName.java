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

/**
 * Represents a name whose recording is stored in a flat file. Includes support for 'bad quality rating'.
 */
public class FileBasedName extends FileBasedRecordingItem<NameInfo> implements Name {

    /**
     * The location of the bad quality file.
     */
    private static final Path BAD_QUALITY_FILE = Paths.get("bad_quality.txt");

    private boolean isBadQuality;

    public FileBasedName(NameInfo nameInfo, Path basePath, RecordingFileResolver<NameInfo> resolver, AudioSystem audioSystem) {
        super(nameInfo, basePath, resolver, audioSystem);
        setUpBadQuality();
    }

    /**
     * @return true if this name's recording has been marked as bad quality.
     */
    @Override
    public boolean isBadQuality() {
        return isBadQuality;
    }

    /**
     * Mark this recording as bad quality (or not). The result will be automatically saved to the bad quality rating file.
     *
     * @param value true to mark this recording as bad quality; false for the default (i.e. good quality)
     */
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
     * Determine whether this recording was previously marked as bad quality, based on the current contents of the
     * bad quality file.
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
