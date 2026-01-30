package org.Garochior.ui;

import javafx.geometry.Pos;
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

public class MainMenu {

    public void start (Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();


        Image img = new Image(getClass().getResourceAsStream("/background/barca_25-26.jpg"));
        ImageView background = new ImageView(img);

        background.setFitHeight(screenHeight);
        background.setFitWidth(screenWidth);

        //Buttons creation
        Button serverBtn = createButton("Create Server");
        Button clientBtn = createButton("Join Server");
        Button exitBtn = createButton("Exit");




        //Layout with buttons
        VBox layout = new VBox(UiConfig.LAYOUT_SPACING, serverBtn, clientBtn, exitBtn);
        VBox.setMargin(exitBtn, new javafx.geometry.Insets(UiConfig.LAYOUT_SPACING, 0, 0, 0));
        VBox serverLayout = serverLayoutSetup();
        VBox clientLayout = clientLayoutSetup();
        layout.setAlignment(Pos.CENTER);

        //Layout for server and client options
        VBox secondBox = new VBox(UiConfig.LAYOUT_SPACING);
        secondBox.getChildren().addAll(serverLayout, clientLayout);
        secondBox.setAlignment(Pos.CENTER);

        //Main layout
        HBox mainBox = new HBox(5, layout, secondBox);
        mainBox.setAlignment(Pos.BASELINE_LEFT);

        //Button actions
        serverBtn.setOnAction(e->{
            System.out.println("Server button clicked");
            setVisibility(serverLayout);
        });
        clientBtn.setOnAction(e->{
            System.out.println("Client button clicked");
            setVisibility(clientLayout);
        });
        exitBtn.setOnAction(e->{
            System.out.println("Exit button clicked");
            stage.close();
        });


        //Arranging buttons and background
        StackPane root = new StackPane();
        root.getChildren().addAll(background, mainBox);

        //Creating scene
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setTitle("Ye");
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: red;" +
                        UiConfig.BUTTON_SIZE
        );
        buttonHoverDetection(button);

        return button;
    }

    private VBox serverLayoutSetup(){
        VBox serverLayout = new VBox(3);

        //Selective port label
        Label port = new Label("Port:");
        port.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        port.setTextFill(Color.YELLOW);

        //Port input field
        TextField tfPort = new TextField();
        tfPort.setMaxWidth(UiConfig.MAX_TEXT_FIELD);


        //Confirmation button
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e->{
            System.out.println("Server will be created on port: " + tfPort.getText());
        });


        HBox portBox = new HBox(2);
        portBox.getChildren().addAll(port, tfPort);
        portBox.setAlignment(Pos.CENTER);

        serverLayout.getChildren().addAll(portBox, confirmBtn);
        serverLayout.setAlignment(Pos.BASELINE_CENTER);
        serverLayout.setVisible(false);

        return serverLayout;
    }
    private VBox clientLayoutSetup(){
        VBox clientLayout = new VBox(4);

        //IP introduction label
        Label ip = new Label("IP:");
        ip.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        ip.setTextFill(Color.YELLOW);

        //Port introduction label
        Label port = new Label("Port:");
        port.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        port.setTextFill(Color.YELLOW);

        //Ip input field
        TextField tfIp = new TextField();
        tfIp.setMaxWidth(UiConfig.MAX_TEXT_FIELD);

        //Port input field
        TextField tfPort = new TextField();
        tfPort.setMaxWidth(UiConfig.MAX_TEXT_FIELD);

        //Confirmation button
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e->{
            System.out.println("You will connect to " +  "(" + tfPort.getText() + ", " + tfIp.getText() + ")");
        });

        HBox ipBox = new HBox(2);
        ipBox.getChildren().addAll(ip, tfIp);
        ipBox.setAlignment(Pos.CENTER);

        HBox portBox = new HBox(2);
        portBox.getChildren().addAll(port, tfPort);
        portBox.setAlignment(Pos.CENTER);


        clientLayout.getChildren().addAll(ipBox, portBox, confirmBtn);
        clientLayout.setAlignment(Pos.BASELINE_CENTER);

        clientLayout.setVisible(false);
        return clientLayout;
    }


    private void setVisibility (VBox layout) {
        boolean visible = !layout.isVisible();
        layout.setVisible(visible);
    }
    private void buttonHoverDetection(Button button) {
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-border-color: #FFF; " +
                    UiConfig.BUTTON_SIZE +
                    "-fx-background-color: blue;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: red;" +
                    UiConfig.BUTTON_SIZE
            );
        });
    }
}
