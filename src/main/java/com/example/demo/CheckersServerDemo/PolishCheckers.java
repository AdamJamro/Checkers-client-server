package com.example.demo.CheckersServerDemo;

public class PolishCheckers extends Game {
    /**
     * Constructs polish checkers game.
     */
    public PolishCheckers() {
        super(10, 10);
    }

//    @Override
//    public void generateBoard() {
//        board = new AbstractPawn[boardHeight][boardWidth];
//        for (int y = 0; y < boardHeight; y++) {
//            for (int x = 0; x < boardWidth; x++){
//                if (y <= 3 && (x+y) % 2 != 0) {
//                    board[x][y] = new Pawn(PawnColor.BLACK);
//                }
//                if (y >= boardHeight-4 && (x+y) % 2 != 0) {
//                    board[x][y] = new Pawn(PawnColor.WHITE);
//                }
//            }
//        }
//    }

//    @Override
//    public boolean noMovesPossible(PawnColor color) {
//        return false;
//    }

    @Override
    protected void throwExceptionWhenLogicBroken(String type, int oldX, int oldY,
                                                 int newX, int newY,
                                                 int killX, int killY,
                                                 Player player)
            throws IllegalStateException {

        //use the original logic
        super.throwExceptionWhenLogicBroken(type,oldX,oldY,newX,newY,killX,killY,player);

        //add "only best moves legal" logic
        if (!board[oldX][oldY].hasComboMark() && assessMove(type, oldX, oldY, newX, newY) < bestMoveValue(currentPlayer))
            throw new IllegalStateException("Unluckily 4 u, every player must play their best move!");
        System.gc();
    }
}
