package com.example.demo.CheckersClienServerTests;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import com.example.demo.CheckersServerDemo.CheckersServerDemo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationTests {

    private final int serverPort = 4440;

    private CheckersServerDemo createServer() throws IOException {
        return new CheckersServerDemo(new ServerSocket(serverPort));
    }

    private CheckersServerDemo runServer() {
        CheckersServerDemo server;
        try {
            server = createServer();
            server.ServerBoot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    @Test
    void connectionTest() throws IOException{

        CheckersClientDemo client1 = null;
        CheckersClientDemo client2 = null;
        CheckersServerDemo server = runServer();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                CheckersDemoApp.main(new String[0]);
//            }
//        });

        client1 = new CheckersClientDemo(new Socket("localhost",serverPort));
        client2 = new CheckersClientDemo(new Socket("localhost",serverPort));
        client1.pushCommand("NORMAL", 1,2,3,4);

        Assertions.assertNotNull(server);
        Assertions.assertNotNull(client1);
        Assertions.assertEquals(client1.in.nextLine(),"VALID_MOVE");


        client2.pushCommand("NORMAL", 1, 0, 4, 3); //invalid move

        Assertions.assertEquals(client2.in.nextLine(), ("move against the rules!"));
    }


    //TODO: MAKE MORE TESTS
}
