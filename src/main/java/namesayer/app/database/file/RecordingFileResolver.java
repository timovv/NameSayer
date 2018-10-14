package namesayer.app.database.file;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Resolves files within a FileBasedRecordingDatabase.
 */
public interface RecordingFileResolver<TInfo> {
    /**
     * Find all names in the given directory.
     *
     * @param base The base directory.
     * @return The list of names found. If no names were found, the list will be empty.
     */
    List<TInfo> getAll(Path base);

    /**
     * Get the NameInfo for the name stored at the given path.
     *
     * @param fileLocation the location of the file
     * @return an optional NameInfo which will be empty if the location did not correspond to a valid name file.
     */
    Optional<TInfo> getInfo(Path fileLocation);

    /**
     * Get the path for a given name.
     *
     * @return The path.
     */
    Path getPathFor(Path basePath, TInfo info);
}
