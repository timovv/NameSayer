package namesayer.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioStub;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.ffmpeg.FFmpegAudioSystem;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.database.file.FileBasedNameSayerDatabase;
import namesayer.app.shop.NameSayerShop;
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
        if (useMockAudio != null && !useMockAudio.equals("false")) {
            audio = new AudioStub();
        } else {
            audio = new FFmpegAudioSystem();
        }

        Path namesRoot = Paths.get("names");
        if (!Files.exists(namesRoot)) {
            Files.createDirectories(namesRoot);
        }

        Path attemptsRoot = Paths.get("attempts");
        if (!Files.exists(attemptsRoot)) {
            Files.createDirectories(attemptsRoot);
        }

        NameSayerDatabase db = new FileBasedNameSayerDatabase(namesRoot, attemptsRoot, audio);

        NameSayerShop shop = new NameSayerShop();
        shop.setBalance(50000);

        MainMenu mainMenu = new MainMenu(db, audio, shop);
        Scene scene = new Scene(mainMenu);
        stage.setScene(scene);
        // TODO: Read files to settings
        NameSayerSettings.getInstance().setTheme("main");
        stage.show();
    }
}
