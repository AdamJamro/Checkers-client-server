package com.example.demo.CheckersServerDemo;

public enum PlayerRole implements Comparable<PlayerRole> {
    WHITE("white"), BLACK("black");

    String side;

    private PlayerRole(String side){
        this.side = side;
    }


}
