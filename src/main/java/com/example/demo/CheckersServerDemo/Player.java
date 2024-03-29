package com.example.demo.CheckersServerDemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    private Game game;
    private Player opponent;
    public PawnColor playerColor;
    public Socket socket;
    public Scanner input;
    public PrintWriter output;


    public Player(Socket socket, PawnColor playerColor) throws IOException  {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        this.socket = socket;
        this.playerColor = playerColor;
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

    private void handShake() {


//      System.out.println("player" + this + "setup");

        output.println("WELCOME " + playerColor.side);
        String chosenGameType;
        if (playerColor == PawnColor.WHITE) {
            output.println("MESSAGE Waiting for opponent to connect");

            chosenGameType = input.nextLine();
            output.println("MESSAGE chosen game type is " + chosenGameType);
            this.game = buildGame(chosenGameType);

            game.currentPlayer = this;

            opponent.output.println(chosenGameType); //notify the opponent
        } else {
            this.opponent.opponent = this;
            opponent.output.println("MESSAGE Opponent has joined");
            chosenGameType = input.nextLine(); //wait until game type was chosen
            game = opponent.game;
        }
    }

    private Game buildGame(String gameType) {
        System.out.println("debug:gameType:"+gameType);
        return switch (gameType) {
            case "CLASSIC" -> new ClassicCheckers();
            case "RUSSIAN" -> new RussianCheckers();
            case "POLISH" -> new PolishCheckers();
            default -> throw new IllegalArgumentException("invalid game type chosen!");
        };
    }


    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            String[] params = command.split(":");
            System.out.println("player " + playerColor + " got: " + command);
            if (command.startsWith("QUIT")) {
                opponent.output.println("Opponent has quit");
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
            } else if (command.startsWith("KILL")) {
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
            String movedPieceType = null;
            if (game.board[oldX][oldY] != null)
                movedPieceType = game.board[oldX][oldY].toString();

            //send move request to server
            game.move(type, oldX, oldY, newX, newY, killX, killY, this);

            //if no exception thrown from game.move(..) then notify the client:
            String response = "VALID_MOVE", opponentResponse = "OPPONENT_MOVED";
            if (type.equalsIgnoreCase("KILL")
                    && game.hasToCapture(newX,newY)
                    && game.board[newX][newY].toString().equals(movedPieceType) //if we switched from pawn to king then stop
            ) {
                response = response.concat("_COMBO");
                opponentResponse = opponentResponse.concat("_COMBO");

                //if game.move(..) was called currentPlayer is the enemy,
                //in order to give us our combo turn back we switch again
                game.currentPlayer = game.currentPlayer.getOpponent();
            }
            output.println(response
                    + ":" + type
                    + ":" + oldX + ":" + oldY
                    + ":" + newX + ":" + newY
                    + ":" + killX + ":" + killY);
            opponent.output.println(opponentResponse
                    + ":" + type
                    + ":" + oldX + ":" + oldY
                    + ":" + newX + ":" + newY
                    + ":" + killX + ":" + killY);
            if (game.hasWinner()) {
                output.println("VICTORY");
                opponent.output.println("DEFEAT");
            } else if (game.noMovesPossible(PawnColor.WHITE) && game.noMovesPossible(PawnColor.BLACK)) {
                output.println("DRAW");
                opponent.output.println("DRAW");
            }
        } catch (IllegalStateException e) {
            //notify about exception:
            output.println("INVALID_MOVE:(type):"+oldX+":"+oldY+": " + e.getMessage());
        }
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

}
