package com.example.demo.CheckersClientDemo;

import com.example.demo.CheckersDemo.CheckersDemoApp;
import com.example.demo.CheckersDemo.Tile;
import com.example.demo.CheckersServerDemo.PlayerRole;
import javafx.scene.Group;
import javafx.scene.control.Label;

import java.io.IOException;
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

    public boolean isCurrentPlayer =true;

    /**
     * The main thread of the client will listen for messages from the server. The
     * first message will be a "WELCOME" message in which we receive our mark. Then
     * we go into a loop listening for any of the other messages, and handling each
     * message appropriately. The "VICTORY", "DEFEAT", "DRAW", and
     * "OTHER_PLAYER_LEFT" messages will ask the user whether or not to play another
     * game. If the answer is no, the loop is exited and the server is sent a "QUIT"
     * message.
     */

    public CheckersClientDemo(Socket socket){
        try {
            this.socket = socket;
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to establish connection with server");
            System.exit(1);
        }
        handShake();
    }

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
            } else {
                playerRole = "black"; //"black";
                isCurrentPlayer = false;
            }
        }

    }

    private void safeClose(Socket socket) throws IOException {
        if (socket != null)
            socket.close();
    }

    public void receiveMessageFromServer(Tile[][] board, Group pieceGroup, Label msgLabel){
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//               System.out.println("IM IN LISTENING THREAD!");
                if (!isCurrentPlayer && in.hasNextLine()){
                    System.out.println("IM LISTENING!");

                    try {
                        String msg = in.nextLine();
                        System.out.println("just received:  "+msg);
                        if (msg.startsWith("OPPONENT_MOVED")){
                            CheckersDemoApp.updateBoard(msg, board, pieceGroup, playerRole);
                            isCurrentPlayer = true;
                        } else if(msg.startsWith("MESSAGE")) {
                            System.out.println(msg);
                            CheckersDemoApp.updateLabel(msg, msgLabel);
                        } else if(msg.startsWith("VICTORY") || msg.startsWith("DEFEAT")) {
                            System.out.println(msg);
                            isCurrentPlayer = false;
                            socket.close();
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
}