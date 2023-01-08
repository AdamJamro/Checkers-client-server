package com.example.demo.CheckersServerDemo;

public abstract class AbstractPawn {
    protected final PawnColor color;

    public static final int COMBO_OFF = 0, COMBO_ON = 1;
    private int comboMark = COMBO_OFF;


    public AbstractPawn(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return this.color;
    }

    public boolean hasComboMark(){
        return comboMark == 1;
    }

    public void setComboMark(int flag) {
        if (flag != COMBO_ON && flag != COMBO_OFF ){
            throw new IllegalArgumentException("tried to set invalid combo mark");
        }

        comboMark = flag;
    }
}
