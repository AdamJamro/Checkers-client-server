package com.example.demo.CheckersServerDemo;

import java.util.Arrays;
import java.util.Objects;

public abstract class Game {

    //reference to the Player which has to move next
    Player currentPlayer;

    private final int boardWidth;
    private final int boardHeight;

    // grid n by m, null if empty, WHITE whilst being occupied by a white pawn, BLACK - black pawn
    PlayerRole[][] board;

    protected Game(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        board = new PlayerRole[boardWidth][boardHeight];
    }

    public boolean hasWinner() {
        return Arrays.stream(board).anyMatch(p -> Arrays.stream(p).anyMatch(x -> Objects.equals(x.side, currentPlayer.playerRole.side)));
    }


    public synchronized void move(int xLocation, int yLocation, Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[xLocation][yLocation] != null) {
            throw new IllegalStateException("Cell already occupied");
        }
        board[xLocation][yLocation] = currentPlayer.playerRole;
        currentPlayer = currentPlayer.getOpponent();
    }


}
