package com.example.demo.CheckersDemo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

import static java.lang.System.exit;

public class ModalPopupWindow {
    public static void display(String title, String message, String details) {
        Stage popup = new Stage();

        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);
        popup.setMinWidth(500);
        popup.setMinHeight(420);
        popup.setResizable(false);

        Label messageLabel = new Label();
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setText(message);
        messageLabel.setId("messageLabel");

        Button closeBtn = new Button("Ok");
        closeBtn.setScaleX(2);
        closeBtn.setScaleY(2);
        closeBtn.setTranslateY(20);
        closeBtn.setOnAction(e -> exit(0));

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(messageLabel);

        for (String detail : details.split(":")){
            Label tmpLabel = new Label(detail);
            tmpLabel.setMaxWidth(Double.MAX_VALUE);
            layout.getChildren().add(tmpLabel);
        }

        layout.getChildren().addAll(closeBtn, new Label());

        Scene scene = new Scene(layout);
        String css = Objects.requireNonNull(ModalPopupWindow.class.getResource("/css/popup.css")).toExternalForm();
        scene.getStylesheets().add(css);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
