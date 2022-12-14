package com.example.demo.Server;

public abstract class AbstractBoard {
    protected Tile[][] tiles;
    public abstract void generateBoard(); //generate board - set size and starting pawns
    public abstract void setPrimaryPawns();

    AbstractBoard() {
        generateBoard();
    }
}
