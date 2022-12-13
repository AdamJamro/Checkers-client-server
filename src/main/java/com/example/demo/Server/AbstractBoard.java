package com.example.demo.Server;

public abstract class AbstractBoard {

    protected Tile[][] tiles;
    public abstract void generateBoard();
    public abstract void setPrimaryPawns();

    AbstractBoard() {
        generateBoard();
        setPrimaryPawns();
    }
}
