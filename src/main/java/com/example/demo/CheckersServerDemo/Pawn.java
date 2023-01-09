package com.example.demo.CheckersServerDemo;

public class Pawn extends AbstractPawn {

    private final int dir;

    /**
     * Constructs a pawn of given color.
     * @param color pawn color
     */
    public Pawn(PawnColor color) {
        super(color);
        if (color == PawnColor.BLACK) {
            this.dir = 1;
        }
        else this.dir = -1;
    }

    /**
     * Returns pawn color.
     * @return pawn color
     */
    public PawnColor getColor() {
        return this.color;
    }

    /**
     * Returns direction of the pawn.
     * @return 1 if the pawn is black and -1 if white
     */
    public int getDir() {
        return this.dir;
    }
}
