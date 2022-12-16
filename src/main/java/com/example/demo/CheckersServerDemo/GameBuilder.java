package com.example.demo.CheckersServerDemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameBuilder {

    Socket socket;
    Scanner input;
    PrintWriter output;

    Game game;

    public GameBuilder(Socket socket) throws IOException {
        this.socket = socket;
        build();
    }

    private void build() throws IOException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    public Game getGame() {
        return game;
    }
}
