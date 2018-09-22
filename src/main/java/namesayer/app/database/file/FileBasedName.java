package namesayer.app.database.file;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Attempt;
import namesayer.app.database.Name;
import namesayer.app.database.NameInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FileBasedName implements Name {

    private static final Path BAD_QUALITY_FILE = Paths.get("bad_quality.txt");

    private final NameInfo info;
    private final Path basePath;
    private final Path pathOfThisName;
    private final NameFileResolver resolver;
    private final AudioSystem audioSystem;

    private final ObservableList<Attempt> attempts = FXCollections.observableArrayList();
    private final ObservableList<Attempt> readOnlyAttempts = FXCollections.unmodifiableObservableList(attempts);

    private boolean isBadQuality;

    FileBasedName(NameInfo nameInfo, Path basePath, NameFileResolver resolver, AudioSystem audioSystem) {
        this.resolver = resolver;
        this.audioSystem = audioSystem;
        this.info = nameInfo;
        this.basePath = basePath;
        this.pathOfThisName = resolver.getPathForName(basePath, nameInfo);

        if (!isValid()) {
            throw new NameSayerException("Not a valid path for this resolver");
        }

        setUpBadQuality();
        updateAttempts();
    }

    private void setUpBadQuality() {
        if (!Files.exists(BAD_QUALITY_FILE)) {
            try {
                Files.createFile(BAD_QUALITY_FILE);
            } catch (IOException e) {
                throw new NameSayerException("Could not set up bad quality file", e);
            }
        }

        String entry = pathOfThisName.getFileName().toString();

        try {
            isBadQuality = Files.readAllLines(BAD_QUALITY_FILE).contains(entry);
        } catch (IOException e) {
            isBadQuality = false;
        }
    }

    void updateAttempts() {
        // remove invalids
        Iterator<Attempt> iter = attempts.iterator();
        while(iter.hasNext()) {
            FileBasedAttempt attempt = (FileBasedAttempt)iter.next();
            if(!attempt.isValid()) {
                iter.remove();
            }
        }

        // add new ones
        for (LocalDateTime dt : resolver.getAllAttempts(basePath, info)) {
            if (attempts.stream().noneMatch(x -> x.getAttemptTime().equals(dt))) {
                Path location = resolver.getPathForAttempt(basePath, info, dt);
                attempts.add(new FileBasedAttempt(location, dt, audioSystem));
            }
        }
    }

    @Override
    public NameInfo getNameInfo() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        Optional<NameInfo> info = resolver.getNameInfo(pathOfThisName);
        assert info.isPresent() : "if info was not present the name must be invalid";
        return info.get();
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        return audioSystem.loadAudio(pathOfThisName);
    }

    @Override
    public boolean isBadQuality() {
        return isBadQuality;
    }

    @Override
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
            badQualityFiles.add(pathOfThisName.getFileName().toString());
        } else {
            badQualityFiles.remove(pathOfThisName.getFileName().toString());
        }

        try {
            Files.write(BAD_QUALITY_FILE, badQualityFiles);
        } catch (IOException e) {
            throw new NameSayerException("Could not update bad quality register", e);
        }
    }

    @Override
    public ObservableList<Attempt> getAttempts() {
        return readOnlyAttempts;
    }

    @Override
    public CompletableFuture<Void> addAttempt(AudioClip recording, LocalDateTime creationTime) {
        Path location = resolver.getPathForAttempt(basePath, info, creationTime);

        if (Files.exists(location)) {
            throw new NameSayerException("File already exists for this attempt");
        }

        if (!Files.exists(location.getParent())) {
            try {
                Files.createDirectories(location.getParent());
            } catch (IOException e) {
                throw new NameSayerException("Could not create attempt", e);
            }
        }

        return audioSystem.saveAudio(recording, location)
                .thenRun(() -> Platform.runLater(() -> attempts.add(new FileBasedAttempt(location, creationTime, audioSystem))));
    }

    @Override
    public CompletableFuture<Void> removeAttempt(Attempt toRemove) {
        if (!(toRemove instanceof FileBasedAttempt)) {
            return CompletableFuture.completedFuture(null);
        }

        ((FileBasedAttempt) toRemove).delete();
        Platform.runLater(() -> attempts.remove(toRemove));
        return CompletableFuture.completedFuture(null);
    }

    boolean isValid() {
        return Files.exists(pathOfThisName) && resolver.getNameInfo(pathOfThisName).isPresent();
    }

    void delete() {
        try {
            Files.delete(pathOfThisName);

            for (Attempt attempt : attempts) {
                FileBasedAttempt cast = (FileBasedAttempt) attempt;
                cast.delete();
            }

        } catch (IOException e) {
            throw new NameSayerException("Could not delete name", e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Name)) {
            return false;
        }

        return this.getNameInfo().equals(((FileBasedName) other).getNameInfo());
    }

}
