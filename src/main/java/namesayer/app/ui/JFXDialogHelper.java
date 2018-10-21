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

    /**
     * Create a new JFXDialogHelper with the given information.
     * @param heading The heading for the dialog.
     * @param body The body of the dialog.
     * @param buttonText The text of the dialog's button.
     * @param stackPane The StackPane over which this dialog should be shown.
     */
    public JFXDialogHelper(String heading, String body, String buttonText, StackPane stackPane) {
        JFXDialogLayout contents = new JFXDialogLayout();
        contents.setHeading(new Text(heading));
        contents.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, contents, JFXDialog.DialogTransition.CENTER);

        JFXButton button = new JFXButton(buttonText);
        button.setOnAction(event -> this.dialog.close());
        contents.setActions(button);
    }

    /**
     * Show a dialog with the parameters provided in the constructor.
     */
    public void show() {
        this.dialog.show();
    }

    /**
     * Set the size of the generated JFXDialog.
     * @param width The width the dialog should be.
     * @param height The height the dialog should be.
     */
    public void setSize(double width, double height) {
        this.dialog.setMaxSize(width, height);
    }

    /**
     * Set the scene that should be transitioned to once the dialog closes.
     * @param thisScene The current scene object.
     * @param nextScene The next scene.
     */
    public void setNextScene(Scene thisScene, Parent nextScene) {
        this.dialog.setOnDialogClosed(event -> {
            thisScene.setRoot(nextScene);
        });
    }
}
