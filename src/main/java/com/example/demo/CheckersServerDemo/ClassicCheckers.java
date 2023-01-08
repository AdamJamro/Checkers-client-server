package com.example.demo.CheckersServerDemo;

public class ClassicCheckers extends Game {

    public ClassicCheckers() {
        super(8, 8);
    }

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
