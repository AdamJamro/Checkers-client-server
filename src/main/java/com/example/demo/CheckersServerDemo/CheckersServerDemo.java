package com.example.demo.CheckersServerDemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class CheckersServerDemo {

    private final ServerSocket serverSocket;

    public CheckersServerDemo(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void ServerBoot() {
        System.out.println("Server is running...");
        int ClientIndex = 1;

        try {
            var pool = Executors.newFixedThreadPool(2);

            while (!serverSocket.isClosed()){
                Game game = new ClassicGame();
                pool.execute(game.new Player(serverSocket.accept(), PlayerRole.WHITE, game);
                System.out.println("Client " + ClientIndex++ + " has connected");
                pool.execute(game.new Player(serverSocket.accept(), PlayerRole.BLACK, game));
                System.out.println("Client " + ClientIndex++ + " has connected");
            }
        } catch (IOException e) {
            closeServerSocket();
        }



    }

    public static void main(String[] args) throws Exception {
        try {
            var listener = new ServerSocket(58901);
            new CheckersServerDemo(listener).ServerBoot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void safeClose(Socket socket) throws IOException {
        if (socket != null)
            socket.close();
    }


}
