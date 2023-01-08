package com.example.demo.CheckersClienServerTests;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import com.example.demo.CheckersServerDemo.CheckersServerDemo;
import com.example.demo.CheckersServerDemo.ClassicCheckers;
import com.example.demo.CheckersServerDemo.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//INTEGRALITY TEST
public class CommunicationTest {

    private final int serverPort = 5050;

    public String gameType = "CLASSIC";

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


    @Test
    void connectionTest() throws IOException, InterruptedException {

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
        Assertions.assertTrue(client1.in.nextLine().startsWith("MESSAGE chosen game type is "));

        //trying out different moves
        client1.pushCommand("NORMAL", 0,5,1,4);
        Assertions.assertEquals("VALID_MOVE:NORMAL:0:5:1:4:-1:-1", client1.in.nextLine());
        Assertions.assertEquals("OPPONENT_MOVED:NORMAL:0:5:1:4:-1:-1", client2.in.nextLine());


//        client2.pushCommand("NORMAL", 1, 0, 4, 3); //invalid move but managed by gui app logic
//
//        client1.pushCommand("KILL", 1, 4, 2, 3); //not my turn
//        Assertions.assertEquals("INVALID_MOVE:(type):1:4: Not your turn", client1.in.nextLine());

        client1.in.close();
        client1.out.close();
        client1.getSocket().close();
        client2.in.close();
        client2.out.close();
        client2.getSocket().close();
        server.closeServerSocket();
    }
//    void playGame(){};

    //TODO: MAKE MORE TESTS
}
