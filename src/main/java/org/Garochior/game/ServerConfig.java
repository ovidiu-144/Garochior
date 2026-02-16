package org.Garochior.game;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.logic.*;
import org.Garochior.model.Card;
import org.Garochior.model.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ServerConfig {
    private List<GamePanelController> uiControllers;
    private List<Player> players;
    private Queue<GameLogic> gamesQueue;
    private boolean isOver = true;

    public ServerConfig() {
        this.uiControllers = new ArrayList<>();
        this.players = new ArrayList<>();
        this.gamesQueue = new ArrayDeque<>();
    }

    public void initGame (Stage serverStage) throws Exception {
        Assets.init();

        GamePanel gamePanel = new GamePanel();
        //Player1 este serverul
        createPlayer(gamePanel, serverStage, 0);

        //Aici o sa treabuiasca sa facem legatura cu clientii, fiecare client cu interfata lui
        //Deocamdata facem doar local
        for (int i = 1; i < 4; ++i){
            Stage stage = new Stage();
            createPlayer(gamePanel, stage, i);
        }

        for (int i = 0; i < 4; ++i){
            addListenerPlayers(players.get(i), uiControllers.get(i));
        }
        initGamesQueue();
        startGameType();
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
            return;
        }
        GameLogic game = gamesQueue.poll();
        System.out.println("Starting game: " + game.getName());

        GameSession gameSession = new GameSession(players, game);
        setGameLabels(game.getName());
        gameSession.setHands();
        //setam cartile in mana pentru fiecare player
        for (GamePanelController ctrl: uiControllers){
            ctrl.setHand();
        }
        gameSession.startGame(this::startGameType);
    }


    private void createPlayer(GamePanel gamePanel, Stage stage, int id) throws Exception {
        Player player = new Player(id);
        players.add(player);
        GamePanelController gamePanelController = gamePanel.start(stage, player);
        uiControllers.add(gamePanelController);
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

                    for (int i = 0; i < 4; ++i){
                        int finalI = i;
                        Platform.runLater(() -> uiControllers.get(finalI).setPlayedCards(removedCard, player.getId()));
                    }

                    if (gameEnded == 4){
                        Platform.runLater(() -> {
                            for (int i = 0; i < 4; ++i){
                                uiControllers.get(i).clearPlayedCards();
                            }
                        });
                        gameEnded = 0;
                    }
                }
            }
        });
        player.myTurn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("It's now player " + (player.getId() + 1) + "'s turn.");
                 Platform.runLater(() -> {
                        for (int i = 0; i < 4; ++i){
                            uiControllers.get(i).setTurnLabel(player.getId());
                        }
                 });
            }
        });
    }
    private void setGameLabels (String name){
        for (int i = 0; i < 4; ++i){
            int finalI = i;
            Platform.runLater(() -> uiControllers.get(finalI).setGameLabel(name));
        }
    }
}
