package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.database.AttemptInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A RecordingFileResolver used to resolve attempts in the attempts database.
 * Attempts are saved in the format Name1_Name2_..._d-M-y-H-m-s.wav in the base path.
 */
public class SE206AttemptFileResolver implements RecordingFileResolver<AttemptInfo> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-y-H-m-s");

    /**
     * Get info for each attempt in the attempts database.
     * {@inheritDoc}
     */
    @Override
    public List<AttemptInfo> getAll(Path base) {
        try {
            return Files.list(base)
                    .map(this::getInfo)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new NameSayerException("Could not get attempts", e);
        }
    }

    /**
     * From the given path associated with an attempt, resolve the AttemptInfo object based on the file name.
     * {@inheritDoc}
     */
    @Override
    public Optional<AttemptInfo> getInfo(Path fileLocation) {
        String fileName = fileLocation.getFileName().toString();

        if (!fileName.endsWith(".wav")) {
            return Optional.empty();
        }

        fileName = fileName.substring(0, fileName.lastIndexOf("."));

        String[] items = fileName.split("_");
        String dateStr = items[items.length - 1];
        LocalDateTime date;
        try {
            date = LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }

        List<String> names = new ArrayList<>();
        for (int i = 0; i < items.length - 1; ++i) {
            names.add(items[i]);
        }

        return Optional.of(new AttemptInfo(names, date));
    }

    /**
     * Given the base path and info object, find the path where the attempt with the given information should be saved.
     *
     * @return The path where the attempt with the given info should be saved.
     */
    @Override
    public Path getPathFor(Path basePath, AttemptInfo attemptInfo) {
        return basePath.resolve(String.join("_", attemptInfo.getNames())
                + "_" + formatter.format(attemptInfo.getAttemptTime()) + ".wav");
    }
}
