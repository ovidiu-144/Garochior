package org.Garochior.game;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.logic.*;
import org.Garochior.model.Card;
import org.Garochior.model.Player;
import org.Garochior.network.MessageType;
import org.Garochior.network.NetworkMessage;
import org.Garochior.network.RelayConnection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ServerConfig {
    private List<GamePanelController> uiControllers;
    private List<Player> players;
    private Queue<GameLogic> gamesQueue;
    private boolean isOver = true;

    private RelayConnection relay;

    private int connectedClients = 0;

    public ServerConfig() {
        this.uiControllers = new ArrayList<>();
        this.players = new ArrayList<>();
        this.gamesQueue = new ArrayDeque<>();
    }

    public void initGame (Stage serverStage, String roomCode) throws Exception {
        Assets.init();

        GamePanel gamePanel = new GamePanel();
        //Player1 este serverul

        createPlayer(gamePanel, serverStage, 0);

        //Aici o sa treabuiasca sa facem legatura cu clientii, fiecare client cu interfata lui
        //Deocamdata facem doar local
        for (int i = 1; i < 4; ++i){
            Player player = new Player(i);
            players.add(player);
//          createPlayer(gamePanel, serverStage, i);
        }

        relay = new RelayConnection();
        relay.connect(roomCode);
        relay.setMessageListener(this::OnMessageReceived);

        //startGameType();
    }

    private void OnMessageReceived (com.google.gson.JsonObject message){
        int type = NetworkMessage.getType(message);

        if (type == MessageType.ROOM_READY) {
            connectedClients++;
            int playerId = NetworkMessage.getPlayerId(message);
            System.out.println("Player " + (playerId + 1) + " connected.");

            if (connectedClients == 3) {
                System.out.println("All players connected. Starting game.");
                Platform.runLater(() -> {
                    try {
                        initGamesQueue();
                        setupListeners();
                        startGameType();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        if (type == MessageType.PLAY_CARD){
            int playerId = NetworkMessage.getPlayerId(message);
            int cardIndex = NetworkMessage.getCardIndex(message);
            System.out.println("Player " + (playerId + 1) + " played card index: " + cardIndex);

            players.get(playerId).setSelectedCard(cardIndex);
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
            relay.send(NetworkMessage.gameOver(scores));

            return;
        }
        GameLogic game = gamesQueue.poll();
        System.out.println("Starting game: " + game.getName());

        relay.send(NetworkMessage.gameStart(game.getName()));
        Platform.runLater(() -> {
            uiControllers.get(0).setGameLabel(game.getName());
        });

        GameSession gameSession = new GameSession(players, game, uiControllers);

        gameSession.setHands();
        //setam cartile in mana pentru fiecare player
        for (int i = 0; i < 4; ++i){
            relay.send(NetworkMessage.setHand(i, players.get(i).hand));
        }
        Platform.runLater(() -> {
           uiControllers.get(0).setHand();
        });

        gameSession.startGame(this::startGameType);
    }
    private void setupListeners(){
        addListenerPlayers(players.get(0), uiControllers.get(0));

        for  (int i = 1; i < 4; ++i){
            Player player = players.get(i);
            player.hand.addListener((ListChangeListener<Card>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        Card removedCard = change.getRemoved().getLast();
                        System.out.println("Card removed from player " + (player.getId() + 1) + ": " + removedCard);
                        //Platform.runLater(() -> uiControllers.get(player.getId()).setHand());
                        relay.send(NetworkMessage.cardPlayed(player.getId(), removedCard));
                    }
                }
            });
            player.myTurn.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    relay.send (NetworkMessage.yourTurn(player.getId()));

                    System.out.println("It's now player " + (player.getId() + 1) + "'s turn.");
//                    relay.send(NetworkMessage.yourTurn(player.getId()));
                    Platform.runLater(() -> {
                       uiControllers.get(0).setTurnLabel(player.getId());
                    });
                }
            });
        }
    }



    private int gameEnded = 0;
    private void addListenerPlayers (Player player, GamePanelController gamePanelController){
        player.hand.addListener((ListChangeListener<Card>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    gameEnded++;
                    System.out.println("Card removed from player " + (player.getId() + 1) + ": " + change.getRemoved());

                    Card removedCard = change.getRemoved().getLast();
                    Platform.runLater(gamePanelController::setHand);

//                    for (int i = 0; i < 4; ++i){
//                        int finalI = i;
//                        Platform.runLater(() -> uiControllers.get(finalI).setPlayedCards(removedCard, player.getId()));
//                    }

                    if (gameEnded == 4){
//                        Platform.runLater(() -> {
//                            for (int i = 0; i < 4; ++i){
//                                uiControllers.get(i).clearPlayedCards();
//                            }
//                        });
                        gameEnded = 0;
                    }
                }
            }
        });
        player.myTurn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("It's now player " + (player.getId() + 1) + "'s turn.");
                 Platform.runLater(() -> {
                            gamePanelController.setTurnLabel(player.getId());
                 });
            }
        });
    }
    private void createPlayer(GamePanel gamePanel, Stage stage, int id) throws Exception {
        Player player = new Player(id);
        players.add(player);
        GamePanelController gamePanelController = gamePanel.start(stage, player);
        uiControllers.add(gamePanelController);
    }
}
