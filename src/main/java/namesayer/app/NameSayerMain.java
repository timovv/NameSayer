package namesayer.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.ffmpeg.FFmpegAudioSystem;
import namesayer.app.database.NameDatabase;
import namesayer.app.database.file.FileBasedNameDatabase;
import namesayer.app.ui.MainMenu;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NameSayerMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // set up audio system and name database as used by the application
        AudioSystem audio = new FFmpegAudioSystem();

        Path root = Paths.get("names");
        if(!Files.exists(root)) {
            Files.createDirectories(root);
        }

        NameDatabase db = new FileBasedNameDatabase(root, audio);

        Scene scene = new Scene(new MainMenu(db, audio));
        stage.setScene(scene);
        stage.show();
    }
}
