package com.example.demo.CheckersServerDemo;

public class PolishCheckers extends Game {

    public PolishCheckers() {
        super(10, 10);
    }

    @Override
    public void generateBoard() {
        board = new AbstractPawn[boardHeight][boardWidth];
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++){
                if (y <= 3 && (x+y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.BLACK);
                }
                if (y >= boardHeight-4 && (x+y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.WHITE);
                }
            }
        }
    }

    @Override
    public boolean noMovesPossible(PawnColor color) {
        return false;
    }
}
