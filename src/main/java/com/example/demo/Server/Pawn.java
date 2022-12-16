package com.example.demo.Server;

public class Pawn {

    private final PawnColor color;
    private final int dir;

    public Pawn(PawnColor color) {
        this.color = color;
        if (color == PawnColor.RED) {
            this.dir = -1;
        }
        else this.dir = 1;
    }

    public PawnColor getColor() {
        return this.color;
    }

}
