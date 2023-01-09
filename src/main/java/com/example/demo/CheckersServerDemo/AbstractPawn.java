package com.example.demo.CheckersServerDemo;

public abstract class AbstractPawn {
    protected final PawnColor color;

    public static final int COMBO_OFF = 0, COMBO_ON = 1;
    private int comboMark = COMBO_OFF;

    /**
     * Construct an AbstractPawn with given color.
     * @param color AbstractPawn color
     */
    public AbstractPawn(PawnColor color) {
        this.color = color;
    }

    /**
     * Return AbstractPawn color.
     * @return color of the pawn
     */
    public PawnColor getColor() {
        return this.color;
    }

    /**
     * Check if pawn has combo mark.
     * @return true if pawn has combo mark, false otherwise
     */
    public boolean hasComboMark(){
        return comboMark == 1;
    }

    /**
     * Set combo mark.
     * @param flag
     */
    public void setComboMark(int flag) {
        if (flag != COMBO_ON && flag != COMBO_OFF ){
            throw new IllegalArgumentException("tried to set invalid combo mark");
        }

        comboMark = flag;
    }
}
