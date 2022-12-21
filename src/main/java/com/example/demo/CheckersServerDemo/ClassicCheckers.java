package com.example.demo.CheckersServerDemo;

public class ClassicCheckers extends Game {

    public ClassicCheckers() {
        super(8, 8);
    }

    @Override
    public void generateBoard() {
        board = new AbstractPawn[boardHeight][boardWidth];
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (y <= 2 && (x + y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.BLACK);
                }
                if (y >= boardHeight - 3 && (x + y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.WHITE);
                }
            }
        }
    }
}
