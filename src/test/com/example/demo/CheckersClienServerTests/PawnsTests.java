package com.example.demo.CheckersClienServerTests;
import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import com.example.demo.CheckersServerDemo.CheckersServerDemo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Tests1 {
    ClassicCheckers classicCheckers = new ClassicCheckers();
    PolishCheckers polishCheckers = new PolishCheckers();

    @Test
    void drawBoards() {
        Assertions.assertTrue(classicCheckers.onBoard(3, 5));
        Assertions.assertFalse(classicCheckers.onBoard(8, 9));
        Assertions.assertTrue(polishCheckers.onBoard(9, 9));
        Assertions.assertEquals(PawnColor.WHITE, polishCheckers.getPawn(0,7).getColor());
        Assertions.assertEquals(PawnColor.BLACK, polishCheckers.getPawn(1,0).getColor());
        polishCheckers.showBoard();
    }

    @Test
    void canMoveTest() {
        Assertions.assertFalse(polishCheckers.canMove(0, 8));
        Assertions.assertTrue(classicCheckers.canMove(3, 2));
        classicCheckers.setPawn(2,5, new King(PawnColor.WHITE));
        Assertions.assertTrue(classicCheckers.canMove(2,5));
    }
}