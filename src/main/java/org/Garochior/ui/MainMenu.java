package org.Garochior.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.Garochior.constants.UiConfig;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Card;
import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.Arrays;
import java.util.List;

public class MainMenu {

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
        AnchorPane root = loader.load();

        Group scalableGroup = new Group(root);
        StackPane rootPane = new StackPane(scalableGroup);
        rootPane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(rootPane);

        Runnable updateScale = () -> {
            double w = rootPane.getWidth();
            double h = rootPane.getHeight();
            if (w == 0 || h == 0) return;

            double scaleX = w / 1920.0;
            double scaleY = h / 1080.0;
            double scale = Math.min(scaleX, scaleY);

            scalableGroup.setScaleX(scale);
            scalableGroup.setScaleY(scale);
        };

        rootPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> updateScale.run());

        stage.setTitle("Garochior");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            System.out.println("Fereastra a fost inchisa de jucator. Opresc aplicatia...");
            Platform.exit();
            System.exit(0);
        });

        stage.setMaximized(true);
        stage.show();

        Platform.runLater(() -> Platform.runLater(updateScale));
    }

//    public void start1 (Stage stage) throws Exception {
//
//        //Temporare pentru teste
//        Deck cardDeck = new Deck();
//        Player[] players = {new Player(0), new Player(1), new Player(2), new Player(3)};
//
//        Screen screen = Screen.getPrimary();
//        double screenWidth = screen.getVisualBounds().getWidth();
//        double screenHeight = screen.getVisualBounds().getHeight();
//
//        double scaleX = Screen.getPrimary().getOutputScaleX();
//        double scaleY = Screen.getPrimary().getOutputScaleY();
//
//        System.out.println("Screen width: " + screenWidth);
//        System.out.println("Screen height: " + screenHeight);
//        System.out.println("Scale X: " + scaleX);
//        System.out.println("Scale Y: " + scaleY);
//
//        //Background image
//        Image img = new Image(getClass().getResourceAsStream("/background/barca_25-26.jpg"));
//        ImageView background = new ImageView(img);
//
//       background.setFitHeight(screenHeight);
//        background.setFitWidth(screenWidth);
//
//        //Buttons creation
//        Button serverBtn = createButton("Create Server");
//        Button clientBtn = createButton("Join Server");
//        Button exitBtn = createButton("Exit");
//
//        //Layout with buttons
//        VBox layout = new VBox(UiConfig.LAYOUT_SPACING, serverBtn, clientBtn, exitBtn);
//        VBox.setMargin(exitBtn, new Insets(UiConfig.LAYOUT_SPACING, 0, 0, 0));
//        VBox serverLayout = serverLayoutSetup();
//        VBox clientLayout = clientLayoutSetup();
//        layout.setAlignment(Pos.CENTER);
//
//        //Layout for server and client options
//        VBox secondBox = new VBox(UiConfig.LAYOUT_SPACING);
//        secondBox.getChildren().addAll(serverLayout, clientLayout);
//        secondBox.setAlignment(Pos.CENTER);
//
//        //Main layout
//        HBox mainBox = new HBox(5, layout, secondBox);
//        mainBox.setAlignment(Pos.BASELINE_LEFT);
//
//
//        //Button actions
//        serverBtn.setOnAction(e->{
//            System.out.println("Server button clicked");
//            setVisibility(serverLayout);
//            for (Player player : players) {
//                List<Card> hand = cardDeck.getPlayerCards(player.id);
//                player.setHand(hand);
//            }
//            System.out.println(Arrays.toString(players));
//        });
//        clientBtn.setOnAction(e->{
//            System.out.println("Client button clicked");
//            setVisibility(clientLayout);
//            cardDeck.shuffle();
//        });
//        exitBtn.setOnAction(e->{
//            System.out.println("Exit button clicked");
//            stage.close();
//        });
//
//        //Arranging buttons and background
//        StackPane root = new StackPane();
//        root.getChildren().addAll(background, mainBox);
//
//        //Creating scene
//        Scene scene = new Scene(root, screenWidth, screenHeight);
//
//        //Rescalare la background
//        background.fitWidthProperty().bind(scene.widthProperty());
//        background.fitHeightProperty().bind(scene.heightProperty());
//
//        // Listener pentru a scala fontul global
//        stage.setTitle("Ye");
//        stage.setScene(scene);
//        //stage.setMaximized(true);
//
//        stage.show();
//    }

//    private Button createButton(String text) {
//        Button button = new Button(text);
//        button.setStyle("-fx-background-color: red;" +
//                        UiConfig.BUTTON_SIZE
//        );
//        buttonHoverDetection(button);
//
//        return button;
//    }
//    private VBox serverLayoutSetup(){
//        VBox serverLayout = new VBox(3);
//
//        //Selective port label
//        Label port = new Label("Port:");
//        port.setFont(Font.font("Arial", FontWeight.BOLD, 32));
//        port.setTextFill(Color.YELLOW);
//
//        //Port input field
//        TextField tfPort = new TextField();
//        tfPort.setMaxWidth(UiConfig.MAX_TEXT_FIELD);
//
//
//        //Confirmation button
//        Button confirmBtn = new Button("Confirm");
//        confirmBtn.setOnAction(e->{
//            System.out.println("Server will be created on port: " + tfPort.getText());
//        });
//
//
//        HBox portBox = new HBox(2);
//        portBox.getChildren().addAll(port, tfPort);
//        portBox.setAlignment(Pos.CENTER);
//
//        serverLayout.getChildren().addAll(portBox, confirmBtn);
//        serverLayout.setAlignment(Pos.BASELINE_CENTER);
//        serverLayout.setVisible(false);
//
//        return serverLayout;
//    }
//    private VBox clientLayoutSetup(){
//        VBox clientLayout = new VBox(4);
//
//        //IP introduction label
//        Label ip = new Label("IP:");
//        ip.setFont(Font.font("Arial", FontWeight.BOLD, 32));
//        ip.setTextFill(Color.YELLOW);
//
//        //Port introduction label
//        Label port = new Label("Port:");
//        port.setFont(Font.font("Arial", FontWeight.BOLD, 32));
//        port.setTextFill(Color.YELLOW);
//
//        //Ip input field
//        TextField tfIp = new TextField();
//        tfIp.setMaxWidth(UiConfig.MAX_TEXT_FIELD);
//
//        //Port input field
//        TextField tfPort = new TextField();
//        tfPort.setMaxWidth(UiConfig.MAX_TEXT_FIELD);
//
//        //Confirmation button
//        Button confirmBtn = new Button("Confirm");
//        confirmBtn.setOnAction(e->{
//            System.out.println("You will connect to " +  "(" + tfPort.getText() + ", " + tfIp.getText() + ")");
//        });
//
//        HBox ipBox = new HBox(2);
//        ipBox.getChildren().addAll(ip, tfIp);
//        ipBox.setAlignment(Pos.CENTER);
//
//        HBox portBox = new HBox(2);
//        portBox.getChildren().addAll(port, tfPort);
//        portBox.setAlignment(Pos.CENTER);
//
//
//        clientLayout.getChildren().addAll(ipBox, portBox, confirmBtn);
//        clientLayout.setAlignment(Pos.BASELINE_CENTER);
//
//        clientLayout.setVisible(false);
//        return clientLayout;
//    }
//
//
//    private void setVisibility (VBox layout) {
//        boolean visible = !layout.isVisible();
//        layout.setVisible(visible);
//    }
//    private void buttonHoverDetection(Button button) {
//        button.setOnMouseEntered(e -> {
//            button.setStyle("-fx-border-color: #FFF; " +
//                    UiConfig.BUTTON_SIZE +
//                    "-fx-background-color: blue;"
//            );
//        });
//
//        button.setOnMouseExited(e -> {
//            button.setStyle("-fx-background-color: red;" +
//                    UiConfig.BUTTON_SIZE
//            );
//        });
//    }
}