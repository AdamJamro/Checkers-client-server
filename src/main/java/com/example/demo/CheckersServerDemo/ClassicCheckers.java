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

    @Override
    public boolean noMovesPossible(PawnColor color) {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getColor() == color && canMove(i, j))
                        return false;
                }
            }
        }
        return true;
    }
}
