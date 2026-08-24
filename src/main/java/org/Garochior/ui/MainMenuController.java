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
import org.Garochior.model.Player;
import org.Garochior.network.RelayConnection;

import javax.print.attribute.standard.Finishings;

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
    public HBox serverSlots;
    public Button player2BtnSv;
    public Button player3BtnSv;
    public Button player4BtnSv;
    public Button startServerBtn;
    public Button cancelServerBtn;

    private ServerConfig serverConfig;

    ///-------Main menu buttons

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

    ///-------Server side


    public void onConfirmServerClicked(ActionEvent actionEvent) {
        System.out.println("You will start a server on room " + roomField.getText());
        serverSlots.setVisible(true);
        startServerBtn.setVisible(true);
        cancelServerBtn.setVisible(true);

        serverConfig = new ServerConfig();
        try {
            serverConfig.initGame(roomField.getText(), this);
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        try {
//            serverConfig = new ServerConfig();
//            serverConfig.initGame("a", this);
//            GamePanel gamePanel = new GamePanel();
//            Player hostPlayer = new Player(0); // Host is always human
//            gamePanel.start((Stage) confirmServerBtn.getScene().getWindow(), hostPlayer);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }

    }

    public void onStartServerClicked(ActionEvent actionEvent) {
        serverSlots.setVisible(false);
        startServerBtn.setVisible(false);

        //verificam daca test = "AI"
        boolean[] aiPlayers = new boolean[4];
        aiPlayers[0] = false; // Host is always human
        aiPlayers[1] = player2BtnSv.getText().equals("AI") || player2BtnSv.getText().equals("Set AI");
        aiPlayers[2] = player3BtnSv.getText().equals("AI") || player3BtnSv.getText().equals("Set AI");
        aiPlayers[3] = player4BtnSv.getText().equals("AI") || player4BtnSv.getText().equals("Set AI");

        try {
            serverConfig.startGame((Stage) confirmServerBtn.getScene().getWindow(), aiPlayers);
            System.out.println("Starting server with AI players: " + java.util.Arrays.toString(aiPlayers));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onCancelServerClicked(ActionEvent actionEvent) {
        serverSlots.setVisible(false);
        startServerBtn.setVisible(false);
        cancelServerBtn.setVisible(false);

        serverConfig.disconnect();
    }

    public void onAddAiClientClicked(ActionEvent actionEvent) {
        Button playerBtn = (Button) actionEvent.getSource();
        playerBtn.setText("AI");
        playerBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;" +
                "-fx-font-size: 12px; -fx-background-radius: 10;");
    }


    public void clientConnected (int playerId){

        Button[] serverSlotsButtons = {null, player2BtnSv, player3BtnSv, player4BtnSv};

        System.out.println("Client connected as player " + playerId);

        Button playerBtn = serverSlotsButtons[playerId];
        playerBtn.setDisable(true);
        playerBtn.setText("Player");
        playerBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;" +
                "-fx-font-size: 12px; -fx-background-radius: 10;");
    }

    public void clientDisconnected (int playerId){
        Button[] serverSlotsButtons = {null, player2BtnSv, player3BtnSv, player4BtnSv};
        System.out.println("Client disconnected from player " + playerId + "Setting slot back to Set Ai");

        Button playerBtn = serverSlotsButtons[playerId];
        playerBtn.setDisable(false);
        playerBtn.setText("Set AI");
        playerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px");
    }

    ///-------Client side


    public void onSlotClicked(ActionEvent actionEvent) {
        Button clicked = (Button) actionEvent.getSource();
        int playerId = Integer.parseInt((String) clicked.getUserData());

        System.out.println("You will join a server on room " + clientRoomField.getText() + " as player " + playerId);
        ClientConfig clientConfig = new ClientConfig();
        try {
            clientConfig.initGame((Stage) clicked.getScene().getWindow(), clientRoomField.getText(), playerId);
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
