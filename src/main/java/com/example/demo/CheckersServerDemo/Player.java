package com.example.demo.CheckersServerDemo;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public abstract class Player implements Runnable {
    public final Game game;
    public Player opponent;
    public PlayerRole playerRole;
    public Socket socket;
    public Scanner input;
    PrintWriter output;
    public ClassicPlayer(Socket socket, PlayerRole playerRole, Game game) {
        this.socket = socket;
        this.playerRole = playerRole;
        this.game = (ClassicGame) game;
    }
}
