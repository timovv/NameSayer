package namesayer.app.database.file;

import namesayer.app.NameSayerException;
import namesayer.app.audio.AudioClip;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.Name;
import namesayer.app.database.NameInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FileBasedName implements Name {

    private final Path location;
    private final NameFileResolver resolver;
    private final AudioSystem audioSystem;

    FileBasedName(Path location, NameFileResolver resolver, AudioSystem audioSystem) {
        this.resolver = resolver;
        this.audioSystem = audioSystem;
        this.location = location;

        if (!isValid()) {
            throw new NameSayerException("Not a valid path for this resolver");
        }
    }

    FileBasedName(NameInfo nameInfo, Path rootLocation, NameFileResolver resolver, AudioSystem audioSystem) {
        this.resolver = resolver;
        this.audioSystem = audioSystem;
        this.location = resolver.getPathForName(rootLocation, nameInfo);

        if (!isValid()) {
            throw new NameSayerException("Not a valid path for this resolver");
        }
    }

    @Override
    public NameInfo getNameInfo() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        Optional<NameInfo> info = resolver.getNameInfo(location);
        assert info.isPresent() : "if info was not present the name must be invalid";
        return info.get();
    }

    @Override
    public CompletableFuture<AudioClip> getRecording() {
        if (!isValid()) {
            throw new NameSayerException("Invalid name");
        }

        return audioSystem.loadAudio(location);
    }

    boolean isValid() {
        return Files.exists(location) && resolver.getNameInfo(location).isPresent();
    }

    void delete() {
        try {
            Files.delete(location);
        } catch(IOException e) {
            throw new NameSayerException("Could not delete name", e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Name)) {
            return false;
        }

        return this.getNameInfo().equals(((FileBasedName) other).getNameInfo());
    }

}
