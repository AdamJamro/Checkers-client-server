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
        System.out.println("Server is running... listening on port " + serverSocket.getLocalPort());
        int ClientIndex = 1;

        try {

            var pool = Executors.newFixedThreadPool(2);
            while (!serverSocket.isClosed()){

                Socket clientSocketA = serverSocket.accept();
                System.out.println("Client no." + ClientIndex++ + " has connected");

//              Game game = new GameBuilder(clientSocketA);
                Game game = new ClassicCheckers();
                pool.execute(new Player(clientSocketA, PawnColor.WHITE, game));

//              Player playerA = createPlayer(game, clientSocketA);
//              Player playerB = createPlayer(game, clientSocketB);

                if (!clientSocketA.isConnected () || game == null) {
                    continue;
                }

                Socket clientSocketB = serverSocket.accept();
                System.out.println("Client no." + ClientIndex++ + " has connected");
                pool.execute(new Player(clientSocketB, PawnColor.BLACK, game));

            }
        } catch (IOException e) {
            closeServerSocket();
        }

    }

    public static void main(String[] args) {
        try {
            var listener = new ServerSocket(4545);
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
