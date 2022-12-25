package com.example.demo.CheckersDemo;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

import static com.example.demo.CheckersDemo.Piece.REGULAR_PAWN;
import static com.example.demo.CheckersDemo.PieceType.*;

public class CheckersDemoApp extends Application {

    private static CheckersClientDemo client;
    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private final Tile[][] board = new Tile[WIDTH][HEIGHT];

    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();

    private final Label msgLabel = new Label("Hello Checkers");

//  private AudioClip clip = null;

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
                    piece = makePiece(client.getPlayerRole().equalsIgnoreCase("WHITE")? BLACK : WHITE, x, y);
                }

                if (y >= WIDTH - 3 && (x + y) % 2 != 0) {
                    piece = makePiece(client.getPlayerRole().equalsIgnoreCase("WHITE")? WHITE : BLACK, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;
    }

    //process potential move request to server
    private MoveResult tryMove(Piece piece, int newX, int newY) {

        System.out.println("trying to move piece to:  " + newX + ", " + newY);
        System.out.println("piece type: "+piece.getType().toString());
        System.out.println("client role type: "+client.getPlayerRole());

        if (board[newX][newY].hasPiece()
                || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        int deltaX = newX - x0;
        int deltaY = newY - y0;

        if ((Math.abs(deltaX) == -deltaY && (deltaY) >= -2 && piece.getGamemode() == REGULAR_PAWN)
            || (Math.abs(deltaX) == Math.abs(deltaY) && (deltaY) != 0 && piece.getGamemode() == Piece.KING_PAWN)) {

            int x1 = x0;
            int y1 = y0;
            int stepX = deltaX/Math.abs(deltaX);
            int stepY = deltaY/Math.abs(deltaY);
            int hurdles = Math.abs(deltaY);

            while (x1 != newX && y1 != newY){
                x1 += stepX;
                y1 += stepY;
                if (board[x1][y1].hasPiece()) {
                    if (board[x1][y1].getPiece().getType() != piece.getType()
                            && x1+stepX == newX
                            && y1+stepY == newY){
                        return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                    }
                    return new MoveResult(MoveType.NONE);
                }
                hurdles -= 1;

            }

            if (piece.getGamemode() == REGULAR_PAWN
                && deltaY == -2
                && hurdles == 0) {
                return new MoveResult(MoveType.NONE);
            }

            return new MoveResult(MoveType.NORMAL);
        }

//        if (Math.abs(newX - x0) == 1 && (newY - y0) == piece.getType().moveDir ) {
//            return new MoveResult(MoveType.NORMAL);
//        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {
//
//            int x1 = x0 + (newX - x0) / 2;
//            int y1 = y0 + (newY - y0) / 2;
//
//            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
//                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
//            }
//        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            if (!client.isCurrentPlayer
                    || !type.toString().equalsIgnoreCase(client.getPlayerRole())){
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
//                    String response = client.in.nextLine();
//                    System.out.println("received after pushCmd: " + response);
//                    if (!response.equalsIgnoreCase("VALID_MOVE")) {
//                        piece.abortMove();
//                        return;
//                    }

//                    piece.move(newX, newY);
//                    board[x0][y0].setPiece(null);
//                    board[newX][newY].setPiece(piece);
//                    client.isCurrentPlayer = false;

//                  clip().play();
                }
                case KILL -> {
                    Piece otherPiece = result.getPiece();
                    int killX = toBoard(otherPiece.getOldX());
                    int killY = toBoard(otherPiece.getOldY());
                    client.pushCommand("KILL", x0, y0, newX, newY, killX, killY);
//                    System.out.println(client.in.nextLine());

//                    piece.move(newX, newY);
//                    board[x0][y0].setPiece(null);
//                    board[newX][newY].setPiece(piece);

//                  clip().play();

//                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
//                    pieceGroup.getChildren().remove(otherPiece);
//                    client.isCurrentPlayer = false;
                }
            }
        });

        return piece;
    }



    public static void updateBoard(String msg, Tile[][] board, Group pieceGroup, String playerRole){

        String cmd0 = msg.split(":")[0];
        int len = cmd0.length();
//        System.out.println("msg len: "+len);
        msg = msg.substring(len+1);
        String[] commands = msg.split(":");

        int oldX = Integer.parseInt(commands[1]);
        int oldY = Integer.parseInt(commands[2]);

        if (cmd0.startsWith("INVALID_MOVE")){
            int finalOldX = oldX;
            int finalOldY = oldY;
            Platform.runLater(() -> board[finalOldX][finalOldY].getPiece().abortMove());
            return;
        }

        int newX = Integer.parseInt(commands[3]);
        int newY = Integer.parseInt(commands[4]);
        int killX = Integer.parseInt(commands[5]);
        int killY = Integer.parseInt(commands[6]);

        if (playerRole.equalsIgnoreCase("BLACK")){
            oldX = CheckersClientDemo.invertHorizontal(oldX);
            oldY = CheckersClientDemo.invertVertical(oldY);
            newX = CheckersClientDemo.invertHorizontal(newX);
            newY = CheckersClientDemo.invertVertical(newY);
            killX = CheckersClientDemo.invertHorizontal(killX);
            killY = CheckersClientDemo.invertVertical(killY);
        }



        System.out.println("updateBoard:debug");
        int finalOldX = oldX;
        int finalOldY = oldY;
        int finalNewX = newX;
        int finalNewY = newY;
        int finalKillX = killX;
        int finalKillY = killY;
        System.out.println(oldX+":"+oldY+":"+newX+":"+newY);
        System.out.println(finalOldX+":"+finalOldY+":"+finalNewX+":"+finalNewY);
        Platform.runLater(() -> {
            Piece piece = board[finalOldX][finalOldY].getPiece(); //which piece was moved

            piece.move(finalNewX, finalNewY); //update view
            //update logic
            board[finalNewX][finalNewY].setPiece(piece);
            board[finalOldX][finalOldY].setPiece(null);

            if ( commands[0].startsWith("KILL") ){
                Piece otherPiece = board[finalKillX][finalKillY].getPiece();

                board[finalKillX][finalKillY].setPiece(null); //update logic&view
                pieceGroup.getChildren().remove(otherPiece); //update logic
            }


            if (piece.getGamemode() == REGULAR_PAWN){
                if (
                        (
                                ((finalNewY + 1) == HEIGHT)
                                && (piece.getType() ==
                                        (client.getPlayerRole().equalsIgnoreCase("WHITE")
                                                ? BLACK : WHITE))

                        ) || (
                                (finalNewY == 0)
                                && (piece.getType() ==
                                        (client.getPlayerRole().equalsIgnoreCase("WHITE")
                                                ? WHITE : BLACK))
                        )
                    ) {
                        piece.turnIntoKing();
                    }
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
    @Override
    public void init() {
        try {
            client = new CheckersClientDemo(new Socket("localhost", 4545));
            System.out.println("client connected with server");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to establish connection with server");
            System.exit(1);
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