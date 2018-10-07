package namesayer.app.database;

import java.util.List;
import java.util.stream.Collectors;

public interface NameSayerDatabase {

    RecordingDatabase<NameInfo, ? extends Name> getNameDatabase();

    RecordingDatabase<AttemptInfo, ? extends Attempt> getAttemptDatabase();

    default List<Name> getNamesFor(String name) {
        return getNameDatabase().getAll().stream().filter(x -> x.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    default List<Attempt> getAttemptsFor(List<String> names) {
        return getAttemptDatabase().getAll().stream().filter(x ->
                x.getInfo().getNames().stream().map(String::toLowerCase)
                        .collect(Collectors.toList())
                        .equals(names.stream().map(String::toLowerCase).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    default List<Attempt> getAttemptsForNames(List<Name> names) {
        return getAttemptsFor(names.stream().map(Name::getName).collect(Collectors.toList()));
    }
}
