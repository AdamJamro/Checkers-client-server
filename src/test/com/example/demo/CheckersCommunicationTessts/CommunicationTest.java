package com.example.demo.CheckersCommunicationTessts;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import com.example.demo.CheckersServerDemo.CheckersServerDemo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.demo.CheckersClientDemo.CheckersClientDemo.*;

//INTEGRALITY TEST
public class CommunicationTest {

    private final int serverPort = 5050;

    private CheckersServerDemo createServer() throws IOException {
        return new CheckersServerDemo(new ServerSocket(serverPort));
    }

    private CheckersServerDemo runServer()
            throws IOException, InterruptedException {
        final CheckersServerDemo server = createServer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.serverBoot();
            }
        }).start();
        System.out.println("Waiting for server to boot...");
        Thread.sleep(2000);
        return server;
    }

    CheckersClientDemo[] initializeGame(String gameType) throws IOException, InterruptedException {

        CheckersClientDemo client1;
        CheckersClientDemo client2;
        CheckersServerDemo server = runServer();

        client1 = new CheckersClientDemo(new Socket("127.0.0.1",serverPort));
        client1.setPlayerRole("WHITE");
        client2 = new CheckersClientDemo(new Socket("127.0.0.1",serverPort));
        client2.setPlayerRole("BLACK");

        Assertions.assertNotNull(server);
        Assertions.assertNotNull(client1);
        Assertions.assertNotNull(client2);

        //since makeClient method omits hand-shaking we are able to follow through it here:
        Assertions.assertEquals("WELCOME white", client1.in.nextLine());
        Assertions.assertEquals("MESSAGE Waiting for opponent to connect", client1.in.nextLine());
        Assertions.assertEquals("WELCOME black", client2.in.nextLine());
        Assertions.assertEquals("MESSAGE Opponent has joined", client1.in.nextLine());

        //client1 picks game-type
        client1.out.println(gameType);
        Assertions.assertEquals(gameType, client2.in.nextLine());
        client2.out.println(gameType);
        Assertions.assertTrue(client1.in.nextLine().startsWith("MESSAGE chosen game type is " + gameType.toUpperCase()));

        return new CheckersClientDemo[]{client1,client2};

//        client1.in.close();
//        client1.out.close();
//        client1.getSocket().close();
//        client2.in.close();
//        client2.out.close();
//        client2.getSocket().close();
//        server.closeServerSocket();
    }

    void playGame(CheckersClientDemo[] clients){

        Assertions.assertEquals(2, clients.length);

        //init
        CheckersClientDemo client1 = clients[0];
        CheckersClientDemo client2 = clients[1];
        Assertions.assertNotNull(client1);
        Assertions.assertNotNull(client2);

        //play

        //1st TURN
        client1.pushCommand("NORMAL", 0,5,1,4);
        Assertions.assertEquals("VALID_MOVE:NORMAL:0:5:1:4:-1:-1", client1.in.nextLine());
        Assertions.assertEquals("OPPONENT_MOVED:NORMAL:0:5:1:4:-1:-1", client2.in.nextLine());
        //END OF TURN

        //NEXT TURN
        client1.pushCommand("KILL", 1, 4, 2, 3); //not my turn
        Assertions.assertEquals("INVALID_MOVE:(type):1:4: Not your turn", client1.in.nextLine());

        client2.pushCommand("NORMAL", 1, 0, 4, 3); //invalid move
        Assertions.assertEquals("INVALID_MOVE:(type):6:7: It isn't your piece", client2.in.nextLine());

        client2.pushCommand("NORMAL", 0, 2, 1, 3); //invalid move
        Assertions.assertEquals("INVALID_MOVE:(type):7:5: There's no piece to move", client2.in.nextLine());

        client2.pushCommand("NORMAL", 1, 2, 0, 3); //invalid move
        Assertions.assertEquals("INVALID_MOVE:(type):6:5: It isn't your piece", client2.in.nextLine());

        client2.pushCommand("NORMAL", invertHorizontal(1),
                invertVertical(2),
                invertHorizontal(0),
                invertVertical(3)); //valid move
        Assertions.assertEquals("VALID_MOVE:NORMAL:1:2:0:3:-1:-1", client2.in.nextLine());
        Assertions.assertEquals("OPPONENT_MOVED:NORMAL:1:2:0:3:-1:-1", client1.in.nextLine());
        //END OF TURN
    }






}
