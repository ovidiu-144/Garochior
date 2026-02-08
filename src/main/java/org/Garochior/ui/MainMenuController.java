package org.Garochior.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.Garochior.constants.UiConfig;

public class MainMenuController {
    public ImageView background;
    public VBox serverLayout;
    public VBox clientLayout;
    public Button exitBtn;
    public Button serverBtn;
    public Button clientBtn;

    public void onServerClicked() {
        System.out.println("Server button clicked");
        setVisibility(serverLayout);
    }

    public void onClientClicked(ActionEvent actionEvent) {
        setVisibility(clientLayout);
    }

    public void onExitClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    private void setVisibility (VBox layout) {
        boolean visible = !layout.isVisible();
        layout.setVisible(visible);
    }

    public void onButtonEntered(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setStyle("-fx-border-color: #FFF; " +
                UiConfig.BUTTON_SIZE +
                "-fx-background-color: blue;"
        );
    }

    public void onButtonExit(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setStyle("-fx-border-color: #FFF; " +
                UiConfig.BUTTON_SIZE +
                "-fx-background-color: red;"
        );
    }

}
