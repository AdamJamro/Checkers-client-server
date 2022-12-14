package com.example.demo.CheckersServerDemo;

import java.net.Socket;

public class ClassicPlayer extends Player {


    public ClassicPlayer(Socket socket, PlayerRole playerRole, Game game) {
        super(socket, playerRole, game);
    }
}