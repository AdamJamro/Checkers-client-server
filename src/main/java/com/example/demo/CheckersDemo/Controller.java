package com.example.demo.CheckersDemo;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    private static CheckersClientDemo client;


    public void play() {
        if (client.in.hasNextLine()) {
            try {
                var response = client.in.nextLine();
                if (response.startsWith("VALID_MOVE")) {
//                    msgLabel.setText("Valid move, please wait");

                }
//                else if (response.startsWith("OPPONENT_MOVED")) {
//                    var loc = Integer.parseInt(response.substring(15));
//                    board[loc].setText(opponentMark);
//                    board[loc].repaint();
//                    messageLabel.setText("Opponent moved, your turn");
//                } else if (response.startsWith("MESSAGE")) {
//                    messageLabel.setText(response.substring(8));
//                } else if (response.startsWith("VICTORY")) {
//                    JOptionPane.showMessageDialog(frame, "Winner Winner");
//                    break;
//                } else if (response.startsWith("DEFEAT")) {
//                    JOptionPane.showMessageDialog(frame, "Sorry you lost");
//                    break;
//                } else if (response.startsWith("TIE")) {
//                    JOptionPane.showMessageDialog(frame, "Tie");
//                    break;
//                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
//                    JOptionPane.showMessageDialog(frame, "Other player left");
//                    break;
//                }
//
//                out.println("QUIT");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                socket.close();
//                frame.dispose();
            }

        }
    }





}
