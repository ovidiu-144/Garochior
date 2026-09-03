package org.Garochior.game;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Card;
import org.Garochior.model.Player;
import org.Garochior.network.MessageType;
import org.Garochior.network.NetworkMessage;
import org.Garochior.network.RelayConnection;

import java.io.IOException;
import java.util.List;

public class ClientConfig {
    private Player player;
    private GamePanelController gamePanelController;
    private RelayConnection relay;
    private int playerId;

    private GamePanel gamePanel;
    private boolean isTablou = false;

    public void initGame (Stage Stage, String roomCode, int playerId) throws Exception {
        Assets.init();

        relay = new RelayConnection();
        relay.connectAsClient(roomCode, playerId);
        this.playerId = playerId;

        System.out.println("Client conectat la relay ca player " + (playerId + 1));

        player = new Player(playerId);
        gamePanel = new GamePanel();

        gamePanelController = gamePanel.start(Stage, player);
        gamePanelController.setOnDisconnect(this::disconnect);

        player.myTurn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("It's now player " + (player.getId() + 1) + "'s turn.");
                waitForCardSelection();
            }
        });

        relay.setMessageListener(this::OnMessageReceived);

        //s-a conectat
        relay.send (NetworkMessage.roomReady(playerId));

        relay.isDisconnected.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                disconnect();
            }
        });

        System.out.println("Client conectat la relay ca player " + (playerId + 1));
    }

    private void OnMessageReceived (com.google.gson.JsonObject message){
        System.out.println("Client received message: " + message);

        String type = NetworkMessage.getType(message);

        switch (type) {
            case MessageType.SET_HAND -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                // Setează mâna doar dacă e a noastră

                if (targetPlayer == playerId) {
                    List<Card> cards = NetworkMessage.getCards(message);
                    Platform.runLater(() -> {
                        player.hand.setAll(cards);
                        gamePanelController.setHand();
                    });
                }
            }

            case MessageType.YOUR_TURN -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                Platform.runLater(() -> {
                    gamePanelController.setTurnLabel(targetPlayer);
                    // Activează tura doar dacă e a noastra
                    if (targetPlayer == playerId) {
                        player.myTurn.set(true);
                        waitForCardSelection();
                    }
                });
            }

            case MessageType.CARD_PLAYED -> {
                int fromPlayer = NetworkMessage.getPlayerId(message);
                Card card = NetworkMessage.getCard(message);
                Platform.runLater(() -> {

                    if (fromPlayer == playerId) {
                        player.hand.remove(card);
                        gamePanelController.setHand();
                        player.myTurn.set(false);
                    }
                    //TODO isTablou modificat printr un mesaj, ca sa stie clientul
                    gamePanelController.setPlayedCards(card, fromPlayer, isTablou);
                });
            }

            case MessageType.INVALID_CARD -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                if (targetPlayer == playerId) {
                    // Serverul a respins cartea, selectează din nou
                    Platform.runLater(() -> {
                        gamePanelController.setTurnLabel(playerId); //
                    });
                    waitForCardSelection();
                }
            }

            case MessageType.HAND_TAKER -> {
                int winnerId = NetworkMessage.getPlayerId(message);
                Platform.runLater(() -> {
                    gamePanelController.showHandTaker(winnerId);

                    if (winnerId == playerId) {
                        int score = NetworkMessage.getScore(message);
                        int lastScore = gamePanelController.getScoreLabel();

                        if (score != lastScore) {
                            gamePanelController.setScoreLabel(score);
                        }
                    }

                    gamePanelController.clearPlayedCards();
                });
            }

            case MessageType.GAME_START -> {
                String gameName = NetworkMessage.getGameName(message);
                Platform.runLater(() -> {
                    gamePanelController.setGameLabel(gameName);
                    gamePanelController.clearPlayedCards();
                });
            }

            case MessageType.TABLOU_GAME -> {
                isTablou = NetworkMessage.getIsTablouGame(message);
                gamePanelController.clearTablouPlayedCards();
                gamePanelController.setTablouMode(isTablou);
            }

            case MessageType.GAME_END -> {
                List<Integer> scores = NetworkMessage.getScores(message);

                Platform.runLater(() -> {
                    // Actualizare scor
                    int score = scores.get(playerId);
                    ///Actualizare interfata cu scorul stanga sus sau ceva la stilu
                    System.out.println("Joc terminat! Scorul tau:  " + score );
                });
            }

            case MessageType.GAME_CYCLE_END -> {
                List<Integer> scores = NetworkMessage.getScores(message);
                gamePanelController.setTablouMode(false);
                Platform.runLater(() -> {
                    gamePanelController.setScoreTable(scores);
                });
                try {
                    Thread.sleep(3000); //lasam 3 secunde
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(gamePanelController::hideScores);
            }

            case MessageType.GAME_OVER -> {
                List<Integer> scores = NetworkMessage.getScores(message);
                int winner = NetworkMessage.getWinner(message);
                Platform.runLater(() -> {
                    // TODO: afișează scorurile finale + interfata de back
                    System.out.println("Joc terminat! Scoruri: " + scores);
                    System.out.println("Winner: " + winner);
                    disconnect();
                });
            }

            case MessageType.HOST_DISCONNECTED -> {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Conexiune pierdută");
                    alert.setHeaderText("Host-ul s-a deconectat");
                    alert.setContentText("Jocul s-a încheiat.");
                    alert.showAndWait();

                    disconnect();

                });
            }
        }
    }

    private void waitForCardSelection() {
        Thread waitThread = new Thread(() -> {
            // Așteaptă click de la user
            synchronized (player.lockCardSelect) {
                player.selectedCard = null;
                while (player.selectedCard == null) {
                    try {
                        player.lockCardSelect.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Trimite cartea la server — NU o scoate din mână
            Card selected = player.selectedCard;
            relay.send(NetworkMessage.cardSelected(playerId, selected));
        });
        waitThread.setDaemon(true);
        waitThread.start();
    }

    private void disconnect() {
        Platform.runLater(() -> {
            try {
                relay.disconnect();
                gamePanel.returnToMainMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
