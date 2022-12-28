package com.example.demo.CheckersDemo;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;


import static com.example.demo.CheckersDemo.CheckersDemoApp.TILE_SIZE;

public class Piece extends StackPane {

    private final PieceType type;
    private double mouseX, mouseY;
    private double oldX, oldY;

    public static final int REGULAR_PAWN = 0, KING_PAWN = 1;
    public static final int COMBO_OFF = 0, COMBO_ON = 1;
    private int gamemode = REGULAR_PAWN;

    private int comboMark = COMBO_OFF;

    public PieceType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);

        Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.03);

        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.BLACK
                ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

    public void turnIntoKing(){
        this.setGamemode(KING_PAWN);

        Ellipse parent = (Ellipse) this.getChildren().get(1);
        double parentWidth = parent.getRadiusX();
        double parentHeight = parent.getRadiusY();

        Ellipse kingMark = new Ellipse(parentWidth * 0.4 , parentHeight * 0.3);
        kingMark.setFill(Color.DARKGOLDENROD);
        kingMark.setTranslateX(parent.getTranslateX());
        kingMark.setTranslateY(parent.getTranslateY());
        kingMark.setVisible(true);
        this.getChildren().add(kingMark);
        System.out.println("KING DEBUG");
    }

    public int getGamemode() {
        return gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public boolean hasComboMark() {
        return comboMark == COMBO_ON;
    }

    public boolean setComboMark(int code) {
        if (code != COMBO_ON && code != COMBO_OFF)
            return false;
        comboMark = code;
        return true;
    }
}