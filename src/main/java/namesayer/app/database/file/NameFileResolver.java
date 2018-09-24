package namesayer.app.database.file;

import namesayer.app.database.NameInfo;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Resolves files within a FileBasedNameDatabase.
 */
public interface NameFileResolver {
    /**
     * Find all names in the given directory.
     *
     * @param base The base directory.
     * @return The list of names found. If no names were found, the list will be empty.
     */
    List<NameInfo> getAllNames(Path base);

    /**
     * Find all attempts for a given name in the given directory
     *
     * @return A list of times of for which the attempts were created. (these uniquely identify the attempts)
     */
    List<LocalDateTime> getAllAttempts(Path base, NameInfo info);

    /**
     * Get the NameInfo for the name stored at the given path.
     *
     * @param fileLocation the location of the file
     * @return an optional NameInfo which will be empty if the location did not correspond to a valid name file.
     */
    Optional<NameInfo> getNameInfo(Path fileLocation);

    /**
     * Get the path for a given name.
     *
     * @return The path.
     */
    Path getPathForName(Path basePath, NameInfo nameInfo);

    /**
     * Get the path for a given attempt for a given name.
     */
    Path getPathForAttempt(Path basePath, NameInfo name, LocalDateTime attemptTime);
}
