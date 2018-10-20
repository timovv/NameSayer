package namesayer.app.database;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A database of recordings and user attempts in the NameSayer program.
 * <p>
 * This database is the central point of access for both user attempts and the names in the NameSayer database,
 * and also contains some useful helper methods.
 */
public interface NameSayerDatabase {

    /**
     * @return the database of names available in the NameSayer application.
     */
    RecordingDatabase<NameInfo, ? extends Name> getNameDatabase();

    /**
     * @return the database of user attempts available in the NameSayer application.
     */
    RecordingDatabase<AttemptInfo, ? extends Attempt> getAttemptDatabase();

    /**
     * Finds all name recordings which are a recording for the given name.
     *
     * @param name The name to find recordings of, for example, "Li".
     * @return The list of all recordings in the database for that name (case-insensitive)
     */
    default List<Name> getNamesFor(String name) {
        return getNameDatabase().getAll().stream().filter(x -> x.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    /**
     * Find all attempts which are attempts for the given list of names.
     *
     * @param names The combined name to find attempts for; case-insensitive.
     * @return All attempts that are attempts for the combined name provided.
     */
    default List<Attempt> getAttemptsFor(List<String> names) {
        return getAttemptDatabase().getAll().stream().filter(x ->
                x.getInfo().getNames().stream().map(String::toLowerCase)
                        .collect(Collectors.toList())
                        .equals(names.stream().map(String::toLowerCase).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * Find all attempts which are attempts for the given list of names.
     *
     * @param names The combined name to find attempts for.
     * @return All attempts that are attempts for the combined name provided.
     */
    default List<Attempt> getAttemptsForNames(List<Name> names) {
        return getAttemptsFor(names.stream().map(Name::getName).collect(Collectors.toList()));
    }
}
