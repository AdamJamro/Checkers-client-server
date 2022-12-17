package com.example.demo.CheckersServerDemo;

public class Pawn extends AbstractPawn {

    private final int dir;

    public Pawn(PawnColor color) {
        super(color);
        if (color == PawnColor.BLACK) {
            this.dir = -1;
        }
        else this.dir = 1;
    }

    public PawnColor getColor() {
        return this.color;
    }

    public int getDir() {
        return this.dir;
    }
}
