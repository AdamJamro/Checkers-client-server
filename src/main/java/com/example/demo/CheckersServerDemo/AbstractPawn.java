package com.example.demo.CheckersServerDemo;

public abstract class AbstractPawn {
    protected final PawnColor color;

    public AbstractPawn(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return this.color;
    }
}
