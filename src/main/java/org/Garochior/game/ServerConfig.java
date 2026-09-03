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
import org.Garochior.ui.MainMenuController;

import java.util.*;

public class ServerConfig {
    private GamePanelController gamePanelController = null;
    private MainMenuController mainMenuController;
    private final List<Player> players;
    private final Queue<GameLogic> gamesQueue;

    private RelayConnection relay;
    private GameSession gameSession;

    private GamePanel gamePanel;


    private int connectedClients = 0;
    private boolean started = false;
    private boolean isTablou = false;

    private int currentPlayer = 0;
    private final List<PlayedCard> currentPlayedCards = new ArrayList<>();

    private int currentGame = 0;
    private int currentCycle = 1;

    record PlayedCard(int playerId, Card card) {}

    public ServerConfig() {
        this.players = new ArrayList<>();
        this.gamesQueue = new ArrayDeque<>();
    }

    public void initGame (String roomCode, MainMenuController mainMenuController) throws Exception {
        this.mainMenuController = mainMenuController;
        Assets.init();

        for (int i = 0; i < 4; ++i){
            Player player = new Player(i);
            players.add(player);
        }

        //Avem 4 Jocuri
        for (int i = 0; i < 4; ++i)
            initGamesQueue(i);

        setupListeners();

        relay = new RelayConnection();
        relay.connectAsHost(roomCode);
        relay.setMessageListener(this::OnMessageReceived);

        //listener pentru deconectare
        relay.isDisconnected.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                disconnect();
            }
        });

    }

    public void startGame (Stage serverStage, boolean[] aiPlayers) throws Exception {
        gamePanel = new GamePanel();

        System.out.println("Starting server with AI players: " + Arrays.toString(aiPlayers));
        //setam daca sunt AI
        for  (int i = 1; i < 4; ++i){
            players.get(i).AiMode = aiPlayers[i];
        }
        players.getFirst().AiMode = true; // Host is always AI for testing purposes

        gamePanelController = gamePanel.start(serverStage, players.getFirst());
        gamePanelController.setOnDisconnect(this::disconnect);

        new Thread(() -> {
            startGameType();
            started = true;
        }).start();
    }


    private void OnMessageReceived (com.google.gson.JsonObject message){
        System.out.println("Server received message: " + message);

        String type = NetworkMessage.getType(message);

        switch (type){
            case MessageType.ROOM_READY -> {
                int playerId = NetworkMessage.getPlayerId(message);
                System.out.println("Player " + (playerId + 1) + " connected.");
                System.out.println("Connected clients: " + connectedClients);

                if (started) {
                    setupReconnectPlayer(playerId);
                }
                else {
                    Platform.runLater(() -> mainMenuController.clientConnected(playerId));
                }
            }

            case MessageType.CLIENT_DISCONNECTED -> {
                int playerId = NetworkMessage.getPlayerId(message);

                if (!started) {
                    Platform.runLater(() -> mainMenuController.clientDisconnected(playerId));
                }
                Player player = players.get(playerId);

                player.AiMode = true;

                synchronized (player.lockCardSelect) {
                    player.lockCardSelect.notifyAll();
                }

                System.out.println("Player " + (playerId + 1) + " disconnected.");
                // Handle player disconnection logic here
//                Platform.runLater(() -> {
//                    Alert alert = new Alert(Alert.AlertType.WARNING);
//                    alert.setTitle("Jucător deconectat");
//                    alert.setContentText("Jucatorul " + (playerId + 1) + " s-a deconectat.");
//                    alert.show();
//                });
            }

            case MessageType.CARD_SELECTED -> {
                int playerId = NetworkMessage.getPlayerId(message);

                Card selectedCard = NetworkMessage.getCard(message);

                int cardIndex = players.get(playerId).hand.indexOf(selectedCard);

                if (cardIndex != - 1) {
                    System.out.println("Card found in hand: " + selectedCard);
                    players.get(playerId).setSelectedCard(cardIndex);
//                    gamePanelController.setPlayedCards(selectedCard, playerId, isTablou);
                } else {
                    System.out.println("Card not found in hand: " + selectedCard);
                }
            }
        }
    }

    private void setupReconnectPlayer (int playerId){
        players.get(playerId).AiMode = false;

        //ii trimitem ca a inceput jocul
        relay.send(NetworkMessage.gameStart(gameSession.getGameName()));
        //ii trimitem cartile pe care le are
        relay.send(NetworkMessage.setHand(playerId, players.get(playerId).hand));
        //ii trimitem a cui e randul
        relay.send(NetworkMessage.yourTurn(currentPlayer)); // firstPlayer

        //TODO:
        // ii trimitem scorul + cartile de la Tablou

        //Tablou
//        relay.send(NetworkMessage.isTablouGame(isTablou));
        
        //scorul


        // ii trimitem cartile jucate in runda curenta
        for (PlayedCard currentPlayedCard : currentPlayedCards) {
            int playerIdPlayed = currentPlayedCard.playerId;
            Card cardPlayed = currentPlayedCard.card;
            relay.send(NetworkMessage.cardPlayed(playerIdPlayed, cardPlayed));
        }
    }

    private void initGamesQueue (int playerTurn){
        gamesQueue.add(new HandsGame(playerTurn));
        gamesQueue.add(new HeartsGame(playerTurn));
        gamesQueue.add(new QueensGame(playerTurn));
        gamesQueue.add(new KingGame(playerTurn));
        gamesQueue.add(new TablouGame(playerTurn));
    }

    //metoda infinita pentru a porni jocul, o sa fie apelata dupa ce toti jucatorii s-au conectat
    public void startGameType (){

        if (gamesQueue.isEmpty()){
            System.out.println("No more games in queue.");
            relay.send(NetworkMessage.gameOver(getScores()));

            System.out.println("Game over. Scores: " + getScores());
            //TODO interfata pentru scor, Winner - ul, buton de back to mainMenu, deconectare de la relay
            return;
        }


        currentGame++;
        GameLogic game = gamesQueue.poll();
        System.out.println("Starting game: " + game.getName());

        if (game instanceof TablouGame tg) {
            tg.setOnInvalidCard(playerId -> {
                relay.send(NetworkMessage.invalidCard(playerId));
            });
            // le scoatem inainte sa il setam
            gamePanelController.clearTablouPlayedCards();
            gamePanelController.setTablouMode(true);
            isTablou = true;
            relay.send(NetworkMessage.isTablouGame(isTablou));
        }


        if (game instanceof ValidationLogic vl) {
            vl.setOnInvalidCard(playerId -> {
                relay.send(NetworkMessage.invalidCard(playerId));
            });
            gamePanelController.setTablouMode(false);
            isTablou = false;
            relay.send(NetworkMessage.isTablouGame(isTablou));
        }

        relay.send(NetworkMessage.gameStart(game.getName()));
        Platform.runLater(() -> {
            gamePanelController.setGameLabel(game.getName());
        });

        gameSession = new GameSession(players, game);

        gameSession.setOnHandTaken(playerId -> {
            int currentScore = players.get(playerId).getScore();
            Platform.runLater(() -> {

                gamePanelController.clearPlayedCards();
                gamePanelController.showHandTaker(playerId);

                int lastScore = gamePanelController.getScoreLabel();

                if (lastScore != currentScore) {
                    if (playerId == 0) {
                        gamePanelController.setScoreLabel(currentScore);
                    }
                }
            });
            relay.send(NetworkMessage.handTaker(playerId, currentScore));

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

        if (currentGame == 6){
            relay.send(NetworkMessage.gameCycleEnd(getScores()));
            System.out.println("Game cycle ended. Scores: " + getScores());

            //ne asiguram cu nu mai apare
            gamePanelController.setTablouMode(false);
//            relay.send (NetworkMessage.gameEnd(getScores()));
            Platform.runLater(() -> {
                List<Integer> scores = getScores();
                gamePanelController.setScoreTable(scores);
            });


            try {
                Thread.sleep(3000); //lasam 3 secunde
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(gamePanelController::hideScores);

            currentGame = 0;
            currentCycle++;
            System.out.println("Current Cycle: " + currentCycle);
        }
        gameSession.startGame(this::startGameType);
    }

    private void setupListeners() {
        // Player 0 (serverul) - listener pe hand pentru UI local + relay
        for (int i = 0; i < 4; i++) {
            Player player = players.get(i);
            boolean isHost = (i == 0); // Player 0 is the host

            player.hand.addListener((ListChangeListener<Card>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        Card removedCard = change.getRemoved().getLast();
                        currentPlayedCards.add(new PlayedCard(player.getId(), removedCard));
                        if (currentPlayedCards.size() == 4 && !isTablou) {
                            currentPlayedCards.clear();
                        }
                        relay.send(NetworkMessage.cardPlayed(player.getId(), removedCard));
                        Platform.runLater(() -> gamePanelController.setPlayedCards(removedCard, player.getId(), isTablou));

                        //e host ul
                        if (isHost) {
                            Platform.runLater(gamePanelController::setHand);
                        }
                    }
                }
            });

            player.myTurn.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    currentPlayer = player.getId();
                    System.out.println("myTurn changed for player: " + player.getId());
                    Platform.runLater(() -> {
//                        System.out.println("Updating turnLabel for player: " + player.getId());
                        gamePanelController.setTurnLabel(player.getId());
                    });
                    relay.send(NetworkMessage.yourTurn(player.getId()));
                }
            });
        }

    }

    private List<Integer> getScores() {
        List<Integer> scores = new ArrayList<>();
        for (Player player: players) {
            scores.add(player.getScore());
        }
        return scores;
    }

    public void disconnect() {
        Platform.runLater(() -> {
            try {
                relay.disconnect();
                players.clear();
                gamesQueue.clear();

                if (gamePanel != null)
                    gamePanel.returnToMainMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
