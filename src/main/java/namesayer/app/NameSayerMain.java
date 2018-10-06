package namesayer.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioStub;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.ffmpeg.FFmpegAudioSystem;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.database.file.FileBasedNameSayerDatabase;
import namesayer.app.ui.MainMenu;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NameSayerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        AudioSystem audio;
        String useMockAudio = System.getProperty("useMockAudio");
        if(useMockAudio != null && !useMockAudio.equals("false")) {
            audio = new AudioStub();
        } else {
            audio = new FFmpegAudioSystem();
        }

        Path namesRoot = Paths.get("names");
        if (!Files.exists(namesRoot)) {
            Files.createDirectories(namesRoot);
        }

        Path attemptsRoot = Paths.get("attempts");
        if(!Files.exists(attemptsRoot)) {
            Files.createDirectories(attemptsRoot);
        }

        NameSayerDatabase db = new FileBasedNameSayerDatabase(namesRoot, attemptsRoot, audio);

        Scene scene = new Scene(new MainMenu(db, audio));
        stage.setScene(scene);
        stage.show();
    }
}
