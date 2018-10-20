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

    private JFXDialog _dialog;

    public JFXDialogHelper(String heading, String body, String buttonText, StackPane stackPane) {
        JFXDialogLayout contents = new JFXDialogLayout();
        contents.setHeading(new Text(heading));
        contents.setBody(new Text(body));

        _dialog = new JFXDialog(stackPane, contents, JFXDialog.DialogTransition.CENTER);

        JFXButton button = new JFXButton(buttonText);
        button.setOnAction(event -> _dialog.close());
        contents.setActions(button);
    }

    public void show() {
        _dialog.show();
    }

    public void setNextScene(Scene thisScene, Parent nextScene) {
        _dialog.setOnDialogClosed(event -> {
            thisScene.setRoot(nextScene);
        });
    }
}
