package namesayer.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import namesayer.app.ui.MainMenu;

public class NameSayerMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new MainMenu());
        stage.setScene(scene);
        stage.show();
    }
}
