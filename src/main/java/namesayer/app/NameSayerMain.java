package namesayer.app;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.app.audio.AudioStub;
import namesayer.app.audio.AudioSystem;
import namesayer.app.audio.ffmpeg.FFmpegAudioSystem;
import namesayer.app.database.NameSayerDatabase;
import namesayer.app.database.file.FileBasedNameSayerDatabase;
import namesayer.app.shop.NameSayerShop;
import namesayer.app.ui.MainMenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NameSayerMain extends Application {

    private static HostServices hostServices;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        hostServices = getHostServices();
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
        MainMenu mainMenu = new MainMenu(db, audio, shop);
        Scene scene = new Scene(mainMenu);
        stage.setScene(scene);
        mainMenu.setScene(scene);

        // force update of theme
        NameSayerSettings.getInstance().setTheme("");
        NameSayerSettings.getInstance().setTheme("main");

        // load previous settings and purchased items
        Path shopDataPath = Paths.get("NameSayer.dat");
        if (Files.exists(shopDataPath)) {
            shop.load(shopDataPath);
        }

        // +10 LipCoins for logging in
        shop.addToBalance(10);

        stage.setOnCloseRequest(x -> {
            try {
                shop.saveTo(shopDataPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }

    public static HostServices hostServices() {
        return hostServices;
    }
}
