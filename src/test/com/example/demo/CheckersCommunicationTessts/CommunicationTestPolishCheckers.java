package com.example.demo.CheckersCommunicationTessts;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.example.demo.CheckersClientDemo.CheckersClientDemo.invertHorizontal;
import static com.example.demo.CheckersClientDemo.CheckersClientDemo.invertVertical;

public class CommunicationTestPolishCheckers extends CommunicationTest {

    @Test
    public void CommunicationTestForPolishCheckers() throws IOException, InterruptedException {
        playGame(initializeGame("POLISH"));
    }

    @Override
    void playGame(CheckersClientDemo[] clients){

        Assertions.assertEquals(2, clients.length);

        //init
        CheckersClientDemo client1 = clients[0];
        CheckersClientDemo client2 = clients[1];
        Assertions.assertNotNull(client1);
        Assertions.assertNotNull(client2);

        //play

        //1st TURN
        client1.pushCommand("NORMAL", 1,6,0,5);
        Assertions.assertTrue(client1.in.nextLine().startsWith("VALID_MOVE:NORMAL"));
        Assertions.assertTrue(client2.in.nextLine().startsWith("OPPONENT_MOVED:NORMAL"));
        //END OF TURN

        //NEXT TURN
        client1.pushCommand("KILL", 1, 4, 2, 3); //not my turn
        Assertions.assertEquals("INVALID_MOVE:(type):1:4: Not your turn", client1.in.nextLine());

        client2.pushCommand("NORMAL", 1, 0, 4, 3); //invalid move
        Assertions.assertEquals("INVALID_MOVE:(type):6:7: It isn't your piece", client2.in.nextLine());

        client2.pushCommand("NORMAL", 0, 2, 1, 3); //invalid move
        Assertions.assertEquals("INVALID_MOVE:(type):7:5: There's no piece to move", client2.in.nextLine());

        client2.pushCommand("NORMAL", invertHorizontal(0),
                invertVertical(3),
                invertHorizontal(1),
                invertVertical(4)); //valid move
        Assertions.assertEquals("VALID_MOVE:NORMAL:0:3:1:4:-1:-1", client2.in.nextLine());
        Assertions.assertEquals("OPPONENT_MOVED:NORMAL:0:3:1:4:-1:-1", client1.in.nextLine());
        //END OF TURN
    }

}
