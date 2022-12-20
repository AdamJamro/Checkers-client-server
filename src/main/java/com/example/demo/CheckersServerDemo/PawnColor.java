package com.example.demo.CheckersServerDemo;

public enum PawnColor implements Comparable<PawnColor> {
    WHITE("white"), BLACK("black");
    String side;
    PawnColor(String side){
        this.side = side;
    }
}
