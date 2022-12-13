package com.example.demo.CheckersServerDemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClassicPlayer extends Player {



    @Override
    public void run() {
        try{
            setup();
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

    private void setup() throws IOException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        output.println("WELCOME " + playerRole.side);
        if (playerRole == PlayerRole.WHITE) {
            game.currentPlayer = this;
            output.println("MESSAGE Waiting for opponent to connect");
        } else {
            opponent = game.currentPlayer;
            opponent.opponent = this;
            opponent.output.println("MESSAGE Your move");
        }
    }

    private void processCommands() {
        while (input.hasNextLine()) {
            var command = input.nextLine();
            if (command.startsWith("QUIT")) {
                return;
            } else if (command.startsWith("MOVE")) {
                processMoveCommand(Integer.parseInt(command.substring(5)));
            }
        }
    }

    private void processMoveCommand(int location) {
        try {
            game.move(location, this);
            output.println("VALID_MOVE");
            opponent.output.println("OPPONENT_MOVED " + location);
            if (game.hasWinner()) {
                output.println("VICTORY");
                opponent.output.println("DEFEAT");
            } else if (game.boardFilledUp()) {
                output.println("TIE");
                opponent.output.println("TIE");
            }
        } catch (IllegalStateException e) {
            output.println("MESSAGE " + e.getMessage());
        }
    }
}