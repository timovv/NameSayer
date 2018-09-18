package namesayer.app.database.file;

import namesayer.app.database.NameInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface NameFileResolver {
    /**
     * Find all names in the given directory.
     * @param base The base directory.
     * @return The list of names found. If no names were found, the list will be empty.
     */
    List<NameInfo> getAllNames(Path base);

    /**
     * Get the NameInfo for the name stored at the given path.
     * @param fileLocation the location of the file
     * @return an optional NameInfo which will be empty if the location did not correspond to a valid name file.
     */
    Optional<NameInfo> getNameInfo(Path fileLocation);

    /**
     * Get the path for a given name.
     * @return The path.
     */
    Path getPathForName(Path basePath, NameInfo nameInfo);
}
