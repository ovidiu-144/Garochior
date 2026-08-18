package org.Garochior.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.Garochior.constants.UiConfig;
import org.Garochior.game.ClientConfig;
import org.Garochior.game.GamePanel;
import org.Garochior.game.ServerConfig;

public class MainMenuController {
    public ImageView background;
    public VBox serverLayout;
    public VBox clientLayout;
    public Button exitBtn;
    public Button serverBtn;
    public Button clientBtn;
    public Button confirmServerBtn;
    public TextField roomField;
    public TextField clientRoomField;
    public Button confirmClientBtn;
    public TextField playerIdField;

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

    private void setVisibility(VBox layout) {
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

    public void onConfirmServerClicked(ActionEvent actionEvent) {
        System.out.println("You will start a server on room " + roomField.getText());
        ServerConfig serverConfig = new ServerConfig();
        try {
            serverConfig.initGame((Stage) confirmServerBtn.getScene().getWindow(), roomField.getText());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onConfirmClientClicked(ActionEvent actionEvent) {
        System.out.println("You will join a server on room " + clientRoomField.getText());
        ClientConfig clientConfig = new ClientConfig();
        try {
            clientConfig.initGame((Stage) confirmClientBtn.getScene().getWindow(), clientRoomField.getText());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
