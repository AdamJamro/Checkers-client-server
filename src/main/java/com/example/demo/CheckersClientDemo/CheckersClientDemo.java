package com.example.demo.CheckersClientDemo;



import javafx.application.Platform;

import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;



/**
 * A client for a multi-player tic tac toe game. Loosely based on an example in
 * Deitel and Deitel’s “Java How to Program” book. For this project I created a
 * new application-level protocol called TTTP (for Tic Tac Toe Protocol), which
 * is entirely plain text. The messages of TTTP are:
 *
 * Client -> Server MOVE <n> QUIT
 *
 * Server -> Client WELCOME <char> VALID_MOVE OTHER_PLAYER_MOVED <n>
 * OTHER_PLAYER_LEFT VICTORY DEFEAT TIE MESSAGE <text>
 */
public class CheckersClientDemo implements Runnable{

    private Socket socket;
    public Scanner in;
    public PrintWriter out;

    /**
     * The main thread of the client will listen for messages from the server. The
     * first message will be a "WELCOME" message in which we receive our mark. Then
     * we go into a loop listening for any of the other messages, and handling each
     * message appropriately. The "VICTORY", "DEFEAT", "DRAW", and
     * "OTHER_PLAYER_LEFT" messages will ask the user whether or not to play another
     * game. If the answer is no, the loop is exited and the server is sent a "QUIT"
     * message.
     */

    public CheckersClientDemo(){
        try {
            socket = new Socket("127.0.0.1", 4545);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to establish connection with server");
            System.exit(1);
        }

        handShake();

        System.out.println("try to run this thread");
    }

    private void handShake(){
        System.out.println("debug1");
        if(in.hasNextLine()){
            System.out.println("debug2");
            var response = in.nextLine();
            var side = response.substring(8);
            System.out.println("HANDSHAKE: " + side);
        }
    }



    private void safeClose(Socket socket) throws IOException {
        if (socket != null)
            socket.close();
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
        while(socket.isConnected()) {
            if(in.hasNextLine())
                System.out.println("in.nextLine: "+in.nextLine());
            else{

                System.out.println("runnable dies");
                break;
            }
        }
        });
    }
}