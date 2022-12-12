package com.example.demo.CheckersDemo;

public class MoveResult {

    private final MoveType type;

    public MoveType getType() {
        return type;
    }

    private final Piece piece;

    public Piece getPiece() {
        return piece;
    }

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Piece piece) {
        this.type = type;
        this.piece = piece;
    }
}