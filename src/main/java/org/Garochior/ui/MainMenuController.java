package org.Garochior.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.Garochior.constants.UiConfig;
import org.Garochior.game.ClientConfig;
import org.Garochior.game.GamePanel;
import org.Garochior.game.ServerConfig;
import org.Garochior.network.RelayConnection;

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

    // Buttons for player slots
    public Button player0Btn;
    public Button player1Btn;
    public Button player2Btn;
    public Button player3Btn;
    //boxu lor
    public HBox playerSlots;

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

    public void onSlotClicked(ActionEvent actionEvent) {
        Button clicked = (Button) actionEvent.getSource();
        int playerId = Integer.parseInt((String) clicked.getUserData());

        System.out.println("You will join a server on room " + clientRoomField.getText() + " as player " + playerId);
        ClientConfig clientConfig = new ClientConfig();
        try {
            clientConfig.initGame((Stage) clicked.getScene().getWindow(), clientRoomField.getText());
            // TODO urmmeaza sa transmit id ul
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSearchRoomClicked(ActionEvent actionEvent) {
        RelayConnection relay = new RelayConnection();
        try {
            int[] playerIds = relay.connectForPlayers(clientRoomField.getText());
            System.out.println("Players in room: " + java.util.Arrays.toString(playerIds));

            Button[] slots = {player0Btn, player1Btn, player2Btn, player3Btn};

            for (int i = 0; i < slots.length; i++) {
                boolean occupied = playerIds[i] == 1; // 1 = ocupat, 0 = liber
                Button btn = slots[i];

                if (occupied) {
                    btn.setText("Occupied");
                    btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;" +
                            "-fx-font-size: 12px; -fx-background-radius: 10;");
                    btn.setDisable(true);
                } else {
                    btn.setText("Available");
                    btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                            "-fx-font-size: 12px; -fx-background-radius: 10;");
                    btn.setDisable(false);
                }
            }

            // Dacă primul slot (host) e liber → dezactivează toate celelalte
            // Nu are sens să te conectezi ca client dacă nu există host
            if (playerIds[0] == 0) {
                for (int i = 1; i < slots.length; i++) {
                    slots[i].setDisable(true);
                    slots[i].setStyle("-fx-background-color: #888; -fx-text-fill: white;" +
                            "-fx-font-size: 12px; -fx-background-radius: 10;");
                }
                player0Btn.setText("Create");
                player0Btn.setDisable(false);
            }

            playerSlots.setVisible(true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
