package namesayer.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioStub;
import namesayer.app.audio.AudioSystem;
import namesayer.app.database.NameDatabase;
import namesayer.app.database.file.FileBasedNameDatabase;
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
        // set up audio system and name database as used by the application
        // AudioSystem audio = new FFmpegAudioSystem();

        // use the stub for testing purposes instead
        AudioSystem audio = new AudioStub();

        Path root = Paths.get("names");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        NameDatabase db = new FileBasedNameDatabase(root, audio);

        Scene scene = new Scene(new MainMenu(db, audio));
        stage.setScene(scene);
        stage.show();
    }
}
