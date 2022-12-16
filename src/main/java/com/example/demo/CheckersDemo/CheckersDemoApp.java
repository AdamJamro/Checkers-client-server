package com.example.demo.CheckersDemo;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class CheckersDemoApp extends Application {

    private CheckersClientDemo client;
    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private final Tile[][] board = new Tile[WIDTH][HEIGHT];

    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();

    private final Label msgLabel = new Label("Hello Checkers");

//    private AudioClip clip = null;


    // create root node for our demo app
    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup, msgLabel);
        msgLabel.setVisible(true);

        // fill out the board with pieces and tiles on their spots
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }


        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            if (!client.isCurrentPlayer){
                piece.abortMove();
                return;
            }

            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE -> piece.abortMove();
                case NORMAL -> {
                    client.pushCommand("NORMAL", x0, y0, newX, newY);
                    System.out.println(client.in.nextLine());
                    //TODO: parse response from server



                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    client.isCurrentPlayer = false;

//                    clip().play();
                }
                case KILL -> {
                    client.pushCommand("KILL", x0, y0, newX, newY);
                    System.out.println(client.in.nextLine());
                    //TODO: parse response from server

                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

//                    clip().play();

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    client.isCurrentPlayer = false;
                }
            }
        });

        return piece;
    }

    public static void updateBoard(String msg, Tile[][] board, Group pieceGroup){

        msg = msg.substring(15);
        String[] commands = msg.split(":");
        int oldX = Integer.parseInt(commands[1]);
        int oldY = Integer.parseInt(commands[2]);
        int newX = Integer.parseInt(commands[3]);
        int newY = Integer.parseInt(commands[4]);
        int killX = Integer.parseInt(commands[5]);
        int killY = Integer.parseInt(commands[6]);

        System.out.println("updateBoard:debug");
        Platform.runLater(() -> {
            Piece piece = board[oldX][oldY].getPiece(); //which piece opponent moved
            piece.move(newX,newY); //update view

            //update logic
            board[newX][newY].setPiece(piece);
            board[oldX][oldY].setPiece(null);
            if ( commands[0].startsWith("KILL") ){
                Piece otherPiece = board[killX][killY].getPiece();
                pieceGroup.getChildren().remove(otherPiece);
                board[killX][killY].setPiece(null); //update view
            }
        } );
        System.out.println("updateBoard:debug2");

    }

    public static void updateLabel(String msg, Label msgLabel){
        Platform.runLater(() -> msgLabel.setText(msg));
    }




//    private AudioClip clip(){
//        if (clip == null) {
//            String src = getClass().getResource("com/example/demo/CheckersDemo/capture.mp3").toString();
//            System.out.println("src " + src);
//            clip = new AudioClip(src);
//        }
//        return clip;
//    }


    public void init() {
        try {
            client = new CheckersClientDemo(new Socket("127.0.0.1", 4545));
            System.out.println("client connected with server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("calling listener thread");
        client.receiveMessageFromServer(board, pieceGroup, msgLabel);
    }


    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(scene);


        System.out.println("debug:show()");
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("debug:launch(args)");
        launch(args);
    }
}