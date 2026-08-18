package org.Garochior.game;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.logic.*;
import org.Garochior.model.Card;
import org.Garochior.model.Player;
import org.Garochior.network.MessageType;
import org.Garochior.network.NetworkMessage;
import org.Garochior.network.RelayConnection;
import java.util.List;

public class ClientConfig {
    private Player player;
    private GamePanelController uiController;
    private RelayConnection relay;
    private int playerId;

    public void initGame (Stage Stage, String roomCode) throws Exception {
        Assets.init();

        relay = new RelayConnection();
        this.playerId = relay.connectAsClient(roomCode);
        System.out.println("Client conectat la relay ca player " + (playerId + 1));

        player = new Player(playerId);
        GamePanel gamePanel = new GamePanel();
        uiController = gamePanel.start(Stage, player);

        player.myTurn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("It's now player " + (player.getId() + 1) + "'s turn.");
                waitForCardSelection();
            }
        });

        relay.setMessageListener(this::OnMessageReceived);

        //s-a conectat
        relay.send (NetworkMessage.roomReady(playerId));
        System.out.println("Client conectat la relay ca player " + (playerId + 1));
    }

    private void OnMessageReceived (com.google.gson.JsonObject message){
        int type = NetworkMessage.getType(message);

        switch (type) {
            case MessageType.SET_HAND -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                // Setează mâna doar dacă e a noastră

                if (targetPlayer == playerId) {
                    List<Card> cards = NetworkMessage.getCards(message);
                    Platform.runLater(() -> {
                        player.hand.setAll(cards);
                        uiController.setHand();
                    });
                }
            }

            case MessageType.YOUR_TURN -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                Platform.runLater(() -> {
                    uiController.setTurnLabel(targetPlayer);
                    // Activează tura doar dacă e a noastră
                    if (targetPlayer == playerId) {
                        player.myTurn.set(true);
                    }
                });
            }

            case MessageType.CARD_PLAYED -> {
                int fromPlayer = NetworkMessage.getPlayerId(message);
                Card card = NetworkMessage.getCard(message);
                Platform.runLater(() -> {
                    uiController.setPlayedCards(card, fromPlayer);

                    if (fromPlayer == playerId) {
                        uiController.setHand();
                    }
                });
            }

            case MessageType.HAND_WINNER -> {
                int winnerId = NetworkMessage.getPlayerId(message);
                Platform.runLater(() -> {
                    uiController.showHandTaker(winnerId);
                });
            }

            case MessageType.GAME_START -> {
                String gameName = NetworkMessage.getGameName(message);
                Platform.runLater(() -> {
                    uiController.setGameLabel(gameName);
                    uiController.clearPlayedCards();
                });
            }

            case MessageType.GAME_OVER -> {
                List<Integer> scores = NetworkMessage.getScores(message);
                Platform.runLater(() -> {
                    // TODO: afișează scorurile finale
                    System.out.println("Joc terminat! Scoruri: " + scores);
                });
            }

            case MessageType.INVALID_CARD -> {
                int targetPlayer = NetworkMessage.getPlayerId(message);
                if (targetPlayer == playerId) {
                    // Serverul a respins cartea, selectează din nou
                    Platform.runLater(() -> {
                        uiController.setTurnLabel(playerId); // "E tura ta" rămâne
                    });
                    // Repornește așteptarea — player.myTurn e încă true
                    waitForCardSelection();
                }
            }
        }

    }

    private void waitForCardSelection() {
        new Thread(() -> {
            Card selected = player.selectCard();
            int index = player.hand.indexOf(selected);
            relay.send(NetworkMessage.playCard(playerId, index));
            player.myTurn.set(false);
        }).start();
    }
}
