package namesayer.app.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Class to help create and show JFXDialogs
 */
public class JFXDialogHelper {

    private JFXDialog dialog;

    public JFXDialogHelper(String heading, String body, String buttonText, StackPane stackPane) {
        JFXDialogLayout contents = new JFXDialogLayout();
        contents.setHeading(new Text(heading));
        contents.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, contents, JFXDialog.DialogTransition.CENTER);

        JFXButton button = new JFXButton(buttonText);
        button.setOnAction(event -> this.dialog.close());
        contents.setActions(button);
    }

    public void show() {
        this.dialog.show();
    }

    public void setSize(double width, double height) {
        this.dialog.setMaxSize(width, height);
    }

    public void setNextScene(Scene thisScene, Parent nextScene) {
        this.dialog.setOnDialogClosed(event -> {
            thisScene.setRoot(nextScene);
        });
    }
}
