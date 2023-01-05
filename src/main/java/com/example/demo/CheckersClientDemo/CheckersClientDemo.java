package com.example.demo.CheckersClientDemo;

import com.example.demo.CheckersDemo.CheckersDemoApp;
import com.example.demo.CheckersDemo.ModalPopupWindow;
import com.example.demo.CheckersDemo.Piece;
import com.example.demo.CheckersDemo.Tile;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A client for a multi-player tic tac toe game. Loosely based on an example in
 * Deitel and Deitel’s “Java How to Program” book. For this project I created a
 * new application-level protocol called TTTP (for Tic Tac Toe Protocol), which
 * is entirely plain text. The messages of TTTP are:
 * Client -> Server MOVE <n> QUIT
 * Server -> Client WELCOME <char> VALID_MOVE OTHER_PLAYER_MOVED <n>
 * OTHER_PLAYER_LEFT VICTORY DEFEAT TIE MESSAGE <text>
 */
public class CheckersClientDemo {

    private Socket socket;
    public Scanner in;
    public PrintWriter out;

    private String playerRole;

    private long totalElapsedTime = 0, currentPlayerElapsedTime = 0;
    private long gameStartTime, start, end;

    public boolean isCurrentPlayer = true;

    /**
     * The main thread of the client will listen for messages from the server. The
     * first message will be a "WELCOME" message in which we receive our mark. Then
     * we go into a loop listening for any of the other messages, and handling each
     * message appropriately. The "VICTORY", "DEFEAT", "DRAW", and
     * "OTHER_PLAYER_LEFT" messages will ask the user whether or not to play another
     * game. If the answer is no, the loop is exited and the server is sent a "QUIT"
     * message.
     */

    public CheckersClientDemo(Socket socket) throws IOException{
        this.socket = socket;
        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream(), true);
        handShake();
    }

    public CheckersClientDemo(){}

    private void handShake(){

        System.out.println("debug1");
        if (in.hasNextLine()){
            System.out.println("debug2");
            var response = in.nextLine();
            var side = response.substring(8);
            System.out.println("HANDSHAKE: " + side);
            if(side.equalsIgnoreCase("white")){ //WHITE
                playerRole = "white";
                isCurrentPlayer = false;
                System.out.println("HANDSHAKE: " + in.nextLine()); //MESSAGE Waiting for opponent...
                System.out.println("HANDSHAKE: " + in.nextLine()); //MESSAGE Opponent has joined...
                isCurrentPlayer = true;
                start = System.nanoTime(); // start counting move time for the first player
            } else {
                playerRole = "black";
                isCurrentPlayer = false;
            }
            gameStartTime = System.nanoTime();
        }

    }

    private void safeClose(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("couldn't close the socket");
            }
        }
    }

    public void receiveMessageFromServer(Tile[][] board, Group pieceGroup, Label msgLabel){
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (in.hasNextLine()){
                    try {
                        String msg = in.nextLine();
                        System.out.println("just received: "+msg);
                        if (msg.startsWith("VALID_MOVE")){

                            String[] commands = msg.split(":");
                            System.out.println(Arrays.toString(commands));
                            int x0 = Integer.parseInt(commands[2]);
                            int y0 = Integer.parseInt(commands[3]);
                            if (playerRole.equalsIgnoreCase("BLACK")){
                                x0 = invertHorizontal(x0);
                                y0 = invertVertical(y0);
                            }

                            if (msg.startsWith("VALID_MOVE_COMBO")) {
                                CheckersDemoApp.setComboFlag(CheckersDemoApp.FLAG_RAISED);
                                board[x0][y0].getPiece().setComboMark(Piece.COMBO_ON);
                            } else {
                                CheckersDemoApp.setComboFlag(CheckersDemoApp.FLAG_DOWN);
                                board[x0][y0].getPiece().setComboMark(Piece.COMBO_OFF);

                                isCurrentPlayer = false;
                                end = System.nanoTime();
                                currentPlayerElapsedTime += end - start;
                            }

                            CheckersDemoApp.updateBoard(msg, board, pieceGroup, playerRole);
                        } else if (msg.startsWith("INVALID_MOVE")) {
                            CheckersDemoApp.updateBoard(msg, board, pieceGroup, playerRole);
                            CheckersDemoApp.updateLabel(msg.substring("INVALID_MOVE:?:?: ".length()), msgLabel);
                        } else if (msg.startsWith("OPPONENT_MOVED")){
                            CheckersDemoApp.updateBoard(msg, board, pieceGroup, playerRole);

                            if (!msg.startsWith("OPPONENT_MOVED_COMBO")) {
                                isCurrentPlayer = true;
                                start = System.nanoTime();
                            }
                        } else if(msg.startsWith("MESSAGE")) {
                            System.out.println(msg);
                            CheckersDemoApp.updateLabel(msg.substring("MESSAGE ".length()), msgLabel);
                        } else if(msg.startsWith("VICTORY") || msg.startsWith("DEFEAT") || msg.startsWith("DRAW")) {
                            System.out.println(msg);
                            isCurrentPlayer = false;
                            end = System.nanoTime();
                            currentPlayerElapsedTime += end - start;
                            totalElapsedTime = end - gameStartTime;
                            Platform.runLater(() -> ModalPopupWindow.display("Results",msg,
                                    "Elapsed move time -> " + String.format("%.1f",currentPlayerElapsedTime / 1_000_000_000.0) + "s:"
                                            + "Total elapsed move time (both players) -> " + String.format("%.1f",totalElapsedTime / 1_000_000_000.0) + "s"));
                            safeClose(socket);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void pushCommand(String command, int oldX, int oldY, int newX, int newY, int killX, int killY) {
        if(playerRole.equalsIgnoreCase("WHITE"))
            out.println(command + ":" + oldX + ":" + oldY + ":" + newX + ":" + newY + ":" + killX + ":" + killY );
        else
            out.println(command + ":" + invertHorizontal(oldX) + ":" + invertHorizontal(oldY)
                    + ":" + invertHorizontal(newX) + ":" + invertVertical(newY)
                    + ":" + invertHorizontal(killX) + ":" + invertVertical(killY) );
    }

    public void pushCommand(String command, int oldX, int oldY, int newX, int newY) {
        if(playerRole.equalsIgnoreCase("WHITE"))
            out.println(command + ":" + oldX + ":" + oldY + ":" + newX + ":" + newY + ":-1:-1");
        else
            out.println(command + ":" + invertHorizontal(oldX) + ":" + invertHorizontal(oldY)
                + ":" + invertHorizontal(newX) + ":" + invertVertical(newY) + ":-1:-1" );
    }

    public static int invertVertical(int coordinate) {
        return CheckersDemoApp.HEIGHT - 1 - coordinate;
    }

    public static int invertHorizontal(int coordinate){
        return CheckersDemoApp.WIDTH - 1 - coordinate;
    }

    public String getPlayerRole(){
        return this.playerRole;
    }

    public void setPlayerRole(String playerRole) { this.playerRole = playerRole; }
}