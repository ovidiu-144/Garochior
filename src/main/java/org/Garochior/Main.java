package org.Garochior;

import javafx.application.Application;
import javafx.stage.Stage;
import org.Garochior.game.GamePanel;
import org.Garochior.ui.MainMenu;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application{
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start(stage);

//        GamePanel gamePanel = new GamePanel();
//        gamePanel.start(stage);
    }
}