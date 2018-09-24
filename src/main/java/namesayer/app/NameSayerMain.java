package namesayer.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.ffmpeg.FFmpegAudioSystem;
import namesayer.app.database.NameDatabase;
import namesayer.app.database.file.FileBasedNameDatabase;
import namesayer.app.ui.MainMenu;

import java.nio.file.Paths;

public class NameSayerMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // set up audio system and name database as used by the application
        AudioSystem audio = new FFmpegAudioSystem();
        NameDatabase db = new FileBasedNameDatabase(Paths.get("names"), audio);

        Scene scene = new Scene(new MainMenu(db, audio));
        stage.setScene(scene);
        stage.show();
    }
}
