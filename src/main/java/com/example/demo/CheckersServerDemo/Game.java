package com.example.demo.CheckersServerDemo;

import java.util.Arrays;
import java.util.Objects;

public abstract class Game {

    //reference to the Player which has to move next
    Player currentPlayer;

    // grid 8x8, null if empty, WHITE whilst being occupied by a white pawn, BLACK - black pawn
    PlayerRole board[][];
    public boolean hasWinner() {
        return Arrays.stream(board).anyMatch(p -> Arrays.stream(p).anyMatch(x -> Objects.equals(x.side, currentPlayer.playerRole.side)));
    }

    public boolean boardFilledUp() {
        return Arrays.stream(board).allMatch(p -> p != null);
    }

    public synchronized void move(int location, ClassicPlayer player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[location] != null) {
            throw new IllegalStateException("Cell already occupied");
        }
        board[location][1] = currentPlayer.playerRole;
        currentPlayer = currentPlayer.opponent;
    }


}
