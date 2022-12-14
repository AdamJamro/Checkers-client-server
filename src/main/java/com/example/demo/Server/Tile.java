package com.example.demo.Server;

public class Tile {
    private Pawn pawn;
    private final int row;
    private final int col;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }
    public Pawn getPawn() {
        return this.pawn;
    }
    public int getRow() { return this.row; }
    public int getCol() { return this.col; }
}
