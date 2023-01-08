package com.example.demo.CheckersServerDemo;

public enum PawnColor implements Comparable<PawnColor> {
    WHITE("white"), BLACK("black");
    final String side;
    PawnColor(String side){
        this.side = side;
    }

    public PawnColor getOpposite() {
        if (side.equalsIgnoreCase("white"))
            return BLACK;
        else
            return WHITE;
    }
}
