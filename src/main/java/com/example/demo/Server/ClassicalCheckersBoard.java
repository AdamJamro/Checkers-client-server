package com.example.demo.Server;

public class ClassicalCheckersBoard extends AbstractBoard {

    private final int SIZE = 8;

    @Override
    public void generateBoard() {
        tiles = new Tile[SIZE][SIZE];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tiles[i][j] = new Tile(i, j);
            }
        }
    }

    @Override
    public void setPrimaryPawns() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < SIZE; j++)
                if ((i + j) % 2 == 1)
                    tiles[i][j].setPawn(new Pawn(PawnColor.RED));
        }
        for (int i = 5; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if ((i + j) % 2 == 1)
                    tiles[i][j].setPawn(new Pawn(PawnColor.WHITE));
        }
    }

    public boolean movePossible(final Tile fromTile, final Tile toTile) { //czy normalny ruch czy bicie
        if (fromTile.getPawn() != null && toTile.getPawn() == null) {
            //if (Math.abs(fromTile.getCol() - toTile.getCol()) == 1 && )
        }
        return false;
    }
    
}
