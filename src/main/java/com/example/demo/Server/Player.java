package com.example.demo.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    int player_id;
    Socket socket;
    Scanner input;
    PrintWriter output;
    //int currentplayer = 1;

    public Player(Socket socket, int player_id) throws IOException {
        this.socket = socket;
        this.player_id = player_id;
    }

    @Override
    public void run() {

    }

    public void setPrimaryState() {

    }

    public void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            //tutaj bedzie przetwarzanie komend
        }
    }

    public void giveTurn() {
        //przesylanie informacji graczom o kolejce
    }
}
