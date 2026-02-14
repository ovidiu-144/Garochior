package org.Garochior.game;

import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.logic.GameSession;
import org.Garochior.logic.HandsGame;
import org.Garochior.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    private List<GamePanelController> uiControllers;
    private List<Player> players;

    public ServerConfig() {
        this.uiControllers = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void initGame (Stage serverStage) throws Exception {
        Assets.init();

        GamePanel gamePanel = new GamePanel();
        //Player1 este serverul
        Player player = new Player(0);
        players.add(player);
        GamePanelController gamePanelController = gamePanel.start(serverStage, player);
        uiControllers.add(gamePanelController);

        //Aici o sa treabuiasca sa facem legatura cu clientii, fiecare client cu interfata lui
        //Deocamdata facem doar local
        for (int i = 1; i < 4; ++i){
            Stage stage = new Stage();
            createPlayer(gamePanel, stage, i);
        }

        startGame();
    }

    //metoda infinita pentru a porni jocul, o sa fie apelata dupa ce toti jucatorii s-au conectat
    public void startGame (){
        //testam un GameSession cu HandsGame
        HandsGame game = new HandsGame();
        GameSession gameSession = new GameSession(players, game);
        gameSession.setHands();
        //setam cartile in mana pentru fiecare player
        for (int i = 0; i < players.size(); ++i){
            uiControllers.get(i).setHand();
        }
        //gameSession.startGame();

    }

    private void createPlayer(GamePanel gamePanel, Stage stage, int id) throws Exception {
        Player player = new Player(id);
        players.add(player);
        GamePanelController gamePanelController = gamePanel.start(stage, player);
        uiControllers.add(gamePanelController);
    }
}
