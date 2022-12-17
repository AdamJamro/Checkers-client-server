package com.example.demo.CheckersServerDemo;

public class ClassicCheckers extends Game {

    public ClassicCheckers() {
        super(8, 8);
    }

    @Override
    public synchronized void move(String type, int oldX, int oldY, int newX, int newY, int killX, int killY, Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[newX][newY] != null) {
            throw new IllegalStateException("Cell already occupied");
        } else if (newX >= boardWidth || newY >= boardHeight) {
            throw new IllegalStateException("Pawns cannot leave the board");
        }
        AbstractPawn pawnToMove = board[oldX][oldY];
        board[oldX][oldY] = null;
        board[newX][newY] = pawnToMove; //currentPlayer.playerColor;

        if (type.equalsIgnoreCase("KILL")) {
            board[killX][killY] = null;
        }

        currentPlayer = currentPlayer.getOpponent();
    }
}

