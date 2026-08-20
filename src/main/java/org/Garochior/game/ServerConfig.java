package org.Garochior.game;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.logic.*;
import org.Garochior.model.Card;
import org.Garochior.model.Player;
import org.Garochior.network.MessageType;
import org.Garochior.network.NetworkMessage;
import org.Garochior.network.RelayConnection;

import java.util.*;

public class ServerConfig {
    private GamePanelController gamePanelController;
    private final List<Player> players;
    private final Queue<GameLogic> gamesQueue;

    private RelayConnection relay;
    private GameSession gameSession;


    private int connectedClients = 0;

    public ServerConfig() {
        this.players = new ArrayList<>();
        this.gamesQueue = new ArrayDeque<>();
    }

    public void initGame (Stage serverStage, String roomCode) throws Exception {
        Assets.init();

        GamePanel gamePanel = new GamePanel();
        //Player1 este serverul

        createPlayer(gamePanel, serverStage);

        //Aici o sa treabuiasca sa facem legatura cu clientii, fiecare client cu interfata lui
        //Deocamdata facem doar local
        for (int i = 1; i < 4; ++i){
            Player player = new Player(i);
            players.add(player);
        }

        initGamesQueue();
        setupListeners();

        relay = new RelayConnection();
        relay.connectAsHost(roomCode);
        relay.setMessageListener(this::OnMessageReceived);

        relay.setPlayer(0);
    }

    private void OnMessageReceived (com.google.gson.JsonObject message){
        System.out.println("Server received message: " + message);


        String type = NetworkMessage.getType(message);

        switch (type){
            case MessageType.ROOM_READY -> {
                connectedClients++;
                int playerId = NetworkMessage.getPlayerId(message);
                System.out.println("Player " + (playerId + 1) + " connected.");

                System.out.println("Connected clients: " + connectedClients);

                if (connectedClients == 3) {
                    System.out.println("All players connected. Starting game.");
                    Platform.runLater(() -> {
                        try {
                            startGameType();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            case MessageType.CARD_SELECTED -> {
                int playerId = NetworkMessage.getPlayerId(message);

                Card selectedCard = NetworkMessage.getCard(message);

                int cardIndex = players.get(playerId).hand.indexOf(selectedCard);

                if (cardIndex != - 1) {
                    System.out.println("Card found in hand: " + selectedCard);
                    players.get(playerId).setSelectedCard(cardIndex);
                    gamePanelController.setPlayedCards(selectedCard, playerId);
                } else {
                    System.out.println("Card not found in hand: " + selectedCard);
                }
            }

            case MessageType.CLIENT_DISCONNECTED -> {
                //int playerId = NetworkMessage.getPlayerId(message);
                int playerId = 10;
                System.out.println("Player " + (playerId + 1) + " disconnected.");
                // Handle player disconnection logic here
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Jucător deconectat");
                    alert.setContentText("Jucatorul " + (playerId + 1) + " s-a deconectat. Jocul se va încheia.");
                    alert.show();
                });
            }
        }
    }


    private void initGamesQueue (){
        gamesQueue.add(new HandsGame());
        gamesQueue.add(new HeartsGame());
        gamesQueue.add(new QueensGame());
        gamesQueue.add(new KingGame());
    }

    //metoda infinita pentru a porni jocul, o sa fie apelata dupa ce toti jucatorii s-au conectat
    public void startGameType (){
        if (gamesQueue.isEmpty()){
            System.out.println("No more games in queue.");
            List<Integer> scores = new ArrayList<>();
            for (Player player: players){
                scores.add(player.getScore());
            }
            //aici o sa avem un gameOver dupa cele 4 jocuri
            //relay.send(NetworkMessage.gameEnd(scores));

            return;
        }
        GameLogic game = gamesQueue.poll();
        System.out.println("Starting game: " + game.getName());

        if (game instanceof ValidationLogic vl) {
            vl.setOnInvalidCard(playerId -> {
                relay.send(NetworkMessage.invalidCard(playerId));
            });
        }

        relay.send(NetworkMessage.gameStart(game.getName()));
        Platform.runLater(() -> {
            gamePanelController.setGameLabel(game.getName());
        });

        gameSession = new GameSession(players, game);

        gameSession.setOnHandTaken(playerId -> {
            Platform.runLater(gamePanelController::clearPlayedCards);
            Platform.runLater(() -> gamePanelController.showHandTaker(playerId));
            relay.send(NetworkMessage.handTaker(playerId));
        });


        gameSession.setHands();
        //setam cartile in mana pentru fiecare player
        for (int i = 0; i < 4; ++i){
            relay.send(NetworkMessage.setHand(i, players.get(i).hand));
        }
        Platform.runLater(() -> {
           gamePanelController.setHand();
        });

        //trimitem scorul dupa fiecare mini game
        relay.send (NetworkMessage.gameEnd(getScores()));
        gameSession.startGame(this::startGameType);
    }

    private void setupListeners() {
        // Player 0 (serverul) - listener pe hand pentru UI local + relay
        players.getFirst().hand.addListener((ListChangeListener<Card>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    Card removedCard = change.getRemoved().getLast();
                    Platform.runLater(gamePanelController::setHand);
                    Platform.runLater(() -> gamePanelController.setPlayedCards(removedCard, 0));
                    relay.send(NetworkMessage.cardPlayed(0, removedCard));
                }
            }
        });
        players.getFirst().myTurn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> gamePanelController.setTurnLabel(0));
            }
        });

        for (int i = 1; i < 4; i++) {
            Player player = players.get(i);

            player.hand.addListener((ListChangeListener<Card>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        Card removedCard = change.getRemoved().getLast();
                        relay.send(NetworkMessage.cardPlayed(player.getId(), removedCard));
                    }
                }
            });

            player.myTurn.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    relay.send(NetworkMessage.yourTurn(player.getId()));
                    Platform.runLater(() -> gamePanelController.setTurnLabel(player.getId()));
                }
            });
        }
    }

    private void createPlayer(GamePanel gamePanel, Stage stage) throws Exception {
        Player player = new Player(0);
        players.add(player);
        gamePanelController = gamePanel.start(stage, player);
    }

    private List<Integer> getScores() {
        List<Integer> scores = new ArrayList<>();
        for (Player player: players) {
            scores.add(player.getScore());
        }
        return scores;
    }
}
