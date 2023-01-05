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

    private Game createGame(){
        return new ClassicCheckers();
    }

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

    private CheckersClientDemo makeClient(Socket socket, String playerRole) throws IOException {
        CheckersClientDemo client = new CheckersClientDemo();
        client.in = new Scanner(socket.getInputStream());
        client.out = new PrintWriter(socket.getOutputStream(), true);
        client.setPlayerRole(playerRole);
        return client;
    }

    @Test
    void connectionTest() throws IOException, InterruptedException {

        CheckersClientDemo client1;
        CheckersClientDemo client2;
        CheckersServerDemo server = runServer();

        client1 = makeClient(new Socket("127.0.0.1",serverPort),"WHITE");
        client2 = makeClient(new Socket("127.0.0.1",serverPort),"BLACK");

        Assertions.assertNotNull(server);
        Assertions.assertNotNull(client1);
        Assertions.assertNotNull(client2);

        //since makeClient method omits hand-shaking we are able to follow through it here:
        Assertions.assertEquals("WELCOME white", client1.in.nextLine());
        Assertions.assertEquals("MESSAGE Waiting for opponent to connect", client1.in.nextLine());
        Assertions.assertEquals("MESSAGE Opponent has joined", client1.in.nextLine());
        //HandShake for the 2nd player
        Assertions.assertEquals("WELCOME black", client2.in.nextLine());

        //trying out different moves
        client1.pushCommand("NORMAL", 0,5,1,4);
        Assertions.assertEquals("VALID_MOVE:NORMAL:0:5:1:4:-1:-1", client1.in.nextLine());
        Assertions.assertEquals("OPPONENT_MOVED:NORMAL:0:5:1:4:-1:-1", client2.in.nextLine());


        client2.pushCommand("NORMAL", 1, 0, 4, 3); //invalid move

        Assertions.assertEquals("INVALID_MOVE:(type):6:7: It isn't your piece", client2.in.nextLine());

        server.closeServerSocket();
    }


    //TODO: MAKE MORE TESTS
}
