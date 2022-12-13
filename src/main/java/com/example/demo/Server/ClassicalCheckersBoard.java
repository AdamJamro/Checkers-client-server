package com.example.demo.Server;

public class ClassicalCheckersBoard extends AbstractBoard {

    @Override
    public void generateBoard() {
        tiles = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    @Override
    public void setPrimaryPawns() {

    }

}
