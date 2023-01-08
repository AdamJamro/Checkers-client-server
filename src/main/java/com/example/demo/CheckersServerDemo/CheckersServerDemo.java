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

    public void serverBoot() {
        System.out.println("Server is running... listening on port " + serverSocket.getLocalPort());
        int ClientIndex = 1;

        try {

            var pool = Executors.newFixedThreadPool(2);
            while (!serverSocket.isClosed()){

                Socket clientSocketA = serverSocket.accept();
                System.out.println("Client no." + ClientIndex++ + " has connected");

                Player playerA = new Player(clientSocketA, PawnColor.WHITE);
//                Game game = buildGame(playerA.getIn());
//                Game game = new ClassicCheckers();
//                System.out.println("server has succeeded a game build");
//                playerA.setGame(game);
                pool.execute(playerA);

                if (!clientSocketA.isConnected()){
                    continue;
                }

                Socket clientSocketB = serverSocket.accept();
                System.out.println("Client no." + ClientIndex++ + " has connected");
                Player playerB = new Player(clientSocketB, PawnColor.BLACK);
                playerB.setOpponent(playerA);
//                playerB.setGame(game);
                pool.execute(playerB);


            }
        } catch (IOException e) {
            closeServerSocket();
        }

    }


    public static void main(String[] args) {
        try {
            var listener = new ServerSocket(4545);
            new CheckersServerDemo(listener).serverBoot();
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
