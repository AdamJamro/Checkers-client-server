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


public class CheckersClientDemo {

    private Socket socket;
    public Scanner in;
    public PrintWriter out;

    private String playerRole;

    public final String[] gameTypes = new String[]{"Classic", "Russian", "Polish"};
    private long totalElapsedTime = 0, currentPlayerElapsedTime = 0;
    private long gameStartTime, start, end;

    public boolean isCurrentPlayer = true;

    public CheckersClientDemo(Socket socket) throws IOException{
        this.socket = socket;
        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream(), true);
//        handShake(); // needs to be invoked
    }

    public CheckersClientDemo(){}

    public void handShake(){

        System.out.println("debug1");
//        if (in.hasNextLine()){
            System.out.println("debug2");
            var response = in.nextLine();
            var side = response.substring(8);
            String gameType;
            System.out.println("HANDSHAKE: " + side);
            if(side.equalsIgnoreCase("white")){ //WHITE
                playerRole = "white";
                isCurrentPlayer = false; //will be set true at the end of this block

                System.out.println("HANDSHAKE: " + in.nextLine()); //MESSAGE Waiting for opponent...
                System.out.println("HANDSHAKE: " + in.nextLine()); //MESSAGE Opponent has joined...
                System.out.println("Choose game type");
                do{
                    gameType = new Scanner(System.in).nextLine();
                } while (Arrays.stream(gameTypes).noneMatch(gameType::equalsIgnoreCase));
                out.println(gameType.toUpperCase());
                System.out.println("HANDSHAKE: " + in.nextLine()); //MESSAGE chosen game type is <?>

                isCurrentPlayer = true;
                start = System.nanoTime(); // start counting move time for the first player
            } else {
                playerRole = "black";
                isCurrentPlayer = false;

                gameType = in.nextLine();
                out.println(gameType); //notify my player-thread worker
                System.out.println("HANDSHAKE: " + gameType); //<?>
            }
        //since this app only partially handles logic only few board size needs to be changed, which is handled below
        CheckersDemoApp.updateGameType(gameType);
        gameStartTime = System.nanoTime(); //begin game clock (will be displayed at the end screen)
//        }

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

                            String[] commands = msg.split(":");
                            System.out.println(Arrays.toString(commands));
                            int x0 = Integer.parseInt(commands[2]);
                            int y0 = Integer.parseInt(commands[3]);
                            if (playerRole.equalsIgnoreCase("BLACK")){
                                x0 = invertHorizontal(x0);
                                y0 = invertVertical(y0);
                            }

                            if (msg.startsWith("OPPONENT_MOVED_COMBO")) {
                                board[x0][y0].getPiece().setComboMark(Piece.COMBO_ON);
                            } else {
                                board[x0][y0].getPiece().setComboMark(Piece.COMBO_OFF);
                                isCurrentPlayer = true;
                                start = System.nanoTime();
                            }

                            CheckersDemoApp.updateBoard(msg, board, pieceGroup, playerRole);
                        } else if(msg.startsWith("MESSAGE")) {
                            System.out.println(msg);
                            CheckersDemoApp.updateLabel(msg.substring("MESSAGE ".length()), msgLabel);
                        } else if(msg.startsWith("VICTORY") || msg.startsWith("DEFEAT") || msg.startsWith("DRAW") || msg.startsWith("OTHER_PLAYER_LEFT")) {
                            System.out.println(msg);
                            isCurrentPlayer = false;
                            end = System.nanoTime();
                            currentPlayerElapsedTime += end - start;
                            totalElapsedTime = end - gameStartTime;
                            Platform.runLater(() -> ModalPopupWindow.display("Results",msg.replace("_"," "),
                                    "Elapsed move time -> " + String.format("%.1f",currentPlayerElapsedTime / 1_000_000_000.0) + "s"
                                            +":"
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

    public Socket getSocket() {
        return socket;
    }
}