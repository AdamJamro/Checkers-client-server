package com.example.demo.CheckersDemo;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

import static java.lang.System.exit;

public class ModalGamePicker extends Dialog<String> {

    public ModalGamePicker(CheckersClientDemo client){
        super();
        this.setTitle("WELCOME");
        ButtonType type = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        if (client.getPlayerRole().equalsIgnoreCase("WHITE"))
            buildPickerUI(client);
        else
            buildConnectUI(client);

        this.getDialogPane().getButtonTypes().add(type);
    }


    private void buildConnectUI(CheckersClientDemo client) {
        Label messageLabel = new Label();
        if(client.in.hasNextLine())
            messageLabel.setText("Existing game found, game type: "+ client.in.nextLine());
        messageLabel.setId("messageLabel");
        Label messageLabel2 = new Label();
        messageLabel2.setText("press ok to enter..");


        VBox layout = new VBox(3);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(messageLabel, messageLabel2, new Label());

        getDialogPane().setContent(layout);
        getDialogPane().getStylesheets().add(getClass().getResource("/css/popup.css").toExternalForm());
    }
    private void buildPickerUI(CheckersClientDemo client) {
        Label messageLabel = new Label();
        messageLabel.setText("CHOOSE GAME TYPE");
        messageLabel.setId("messageLabel");

        Button classicButton = new Button("Classic (8x8)");
        classicButton.setOnMousePressed(e -> {
            CheckersDemoApp.WIDTH = 8;
            CheckersDemoApp.HEIGHT = 8;
            CheckersDemoApp.numOfRowsOccupied = 3;
            client.out.println("Classic");
            exit(0);
        });

        Button russianButton = new Button("Russian (8x8)");
        classicButton.setOnMousePressed(e -> {
            CheckersDemoApp.WIDTH = 8;
            CheckersDemoApp.HEIGHT = 8;
            CheckersDemoApp.numOfRowsOccupied = 3;
            client.out.println("Russian");
        });

        Button polishButton = new Button("Polish (10x10)");
        polishButton.setOnMousePressed(e -> {
            CheckersDemoApp.WIDTH = 10;
            CheckersDemoApp.HEIGHT = 10;
            CheckersDemoApp.numOfRowsOccupied = 4;
            client.out.println("Polish");
        });


        VBox layout = new VBox(5);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(messageLabel, classicButton, russianButton, polishButton, new Label());

        getDialogPane().setContent(layout);
        getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/popup.css")).toExternalForm());
    }
}
