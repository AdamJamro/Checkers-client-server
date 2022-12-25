package com.example.demo.CheckersServerDemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    private final Game game;
    private Player opponent;
    public PawnColor playerColor;
    public Socket socket;
    public Scanner input;
    public PrintWriter output;


    public Player(Socket socket, PawnColor playerColor, Game game) {
        this.socket = socket;
        this.playerColor = playerColor;
        this.game = game;
    }

    public Player getOpponent(){
        return this.opponent;
    }

    @Override
    public void run() {
        try {
            handShake();
            processCommands();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (opponent != null && opponent.output != null) {
                opponent.output.println("OTHER_PLAYER_LEFT");
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handShake() throws IOException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

//      System.out.println("player" + this + "setup");

        output.println("WELCOME " + playerColor.side);
        if (playerColor == PawnColor.WHITE) {
            game.currentPlayer = this;
            output.println("MESSAGE Waiting for opponent to connect");
        } else {
            this.opponent = game.currentPlayer;
            this.opponent.opponent = this;
            opponent.output.println("MESSAGE Opponent has joined");
        }
    }

    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            String[] params = command.split(":");
            System.out.println(command);
            if (command.startsWith("QUIT")) {
                return;
            } else if (command.startsWith("NORMAL")) {
                processMoveCommand(
                        "NORMAL",
                        Integer.parseInt( params[1] ),
                        Integer.parseInt( params[2] ),
                        Integer.parseInt( params[3] ),
                        Integer.parseInt( params[4] ),
                        -1,
                        -1
                );
            } else if ( command.startsWith("KILL") ) {
                int x0, y0, newX, newY, killX, killY;
                processMoveCommand(
                        "KILL",
                        x0=Integer.parseInt( params[1] ),
                        y0=Integer.parseInt( params[2] ),
                        newX=Integer.parseInt( params[3] ),
                        newY=Integer.parseInt( params[4] ),
                        killX=Integer.parseInt( params[5] ), //(newX - x0) > 0 ? newX - 1 : newX + 1,
                        killY=Integer.parseInt( params[6] )//(newY - y0) > 0 ? newY - 1 : newY + 1
                );
            }
        }
    }

    private void processMoveCommand(String type, int oldX, int oldY, int newX, int newY, int killX, int killY) {
        try {
            game.move(type, oldX, oldY, newX, newY, killX, killY, this);
            output.println("VALID_MOVE"
                    + ":" + type
                    + ":" + oldX + ":" + oldY
                    + ":" + newX + ":" + newY
                    + ":" + killX + ":" + killY);
            opponent.output.println("OPPONENT_MOVED"
                    + ":" + type
                    + ":" + oldX + ":" + oldY
                    + ":" + newX + ":" + newY
                    + ":" + killX + ":" + killY);
            if (game.hasWinner()) {
                output.println("VICTORY");
                opponent.output.println("DEFEAT");
            } else if (1==2 /*DRAW CONDITION*/) {
                output.println("DRAW");
                opponent.output.println("DRAW");
            }
        } catch (IllegalStateException e) {
            output.println("INVALID_MOVE:"+oldX+":"+oldY+": " + e.getMessage());
        }
    }
}
