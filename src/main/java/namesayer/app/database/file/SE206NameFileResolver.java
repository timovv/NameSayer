package namesayer.app.database.file;

import namesayer.app.database.NameInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Name file resolver based on the names database supplied to us as part of the project.
 * <p>
 * The format of a given name is [creator]-d-m-y_h-m-s_Name.wav. [creator] is usually se206, but there is one recording
 * where it isn't, so this has been handled.
 */
public class SE206NameFileResolver implements RecordingFileResolver<NameInfo> {

    /**
     * A regex representing the expected name format.
     */
    private static final Pattern FORMAT = Pattern.compile("^(?<creator>\\w+?)_(?<day>\\d{0,2})-(?<month>\\d{0,2})" +
            "-(?<year>\\d{4})_(?<hour>\\d{0,2})-(?<minute>\\d{0,2})-(?<second>\\d{0,2})_(?<name>.+)\\.wav$");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d-M-y_H-m-s");

    /**
     * Get the info object of all names stored in the given folder.
     * {@inheritDoc}
     */
    @Override
    public List<NameInfo> getAll(Path base) {
        try {
            return Files.list(base)
                    .map(this::getInfo)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Get the info object of the name stored at the given location.
     * {@inheritDoc}
     */
    @Override
    public Optional<NameInfo> getInfo(Path fileLocation) {
        String fileName = fileLocation.getFileName().toString();
        Matcher matcher = FORMAT.matcher(fileName);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        int day = Integer.parseInt(matcher.group("day"));
        int month = Integer.parseInt(matcher.group("month"));
        int year = Integer.parseInt(matcher.group("year"));
        int hour = Integer.parseInt(matcher.group("hour"));
        int minute = Integer.parseInt(matcher.group("minute"));
        int second = Integer.parseInt(matcher.group("second"));
        String name = matcher.group("name");
        String creator = matcher.group("creator");

        LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute, second);
        return Optional.of(new NameInfo(name, creator, time));
    }

    /**
     * Get the full path for where the recording of the name with the given info should be stored.
     * {@inheritDoc}
     */
    @Override
    public Path getPathFor(Path basePath, NameInfo nameInfo) {
        return basePath.resolve(getFileName(nameInfo) + ".wav");
    }

    /**
     * Get the file name for the recording with the given info.
     */
    private String getFileName(NameInfo info) {
        return info.getCreator() + "_" + DATE_TIME_FORMATTER.format(info.getCreationTime()) + "_" + info.getName();
    }
}
