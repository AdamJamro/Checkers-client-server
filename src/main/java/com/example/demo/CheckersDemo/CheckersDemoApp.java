package com.example.demo.CheckersDemo;

import com.example.demo.CheckersClientDemo.CheckersClientDemo;
import com.example.demo.CheckersServerDemo.AbstractPawn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import static com.example.demo.CheckersClientDemo.CheckersClientDemo.invertHorizontal;
import static com.example.demo.CheckersClientDemo.CheckersClientDemo.invertVertical;
import static com.example.demo.CheckersDemo.Piece.REGULAR_PAWN;
import static com.example.demo.CheckersDemo.PieceType.*;

public class CheckersDemoApp extends Application {

    private static CheckersClientDemo client;
    public static int TILE_SIZE = 100;
    public static int WIDTH = 8;
    public static int HEIGHT = 8;

    public static int numOfRowsOccupied = 0;
    private Tile[][] board;

    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();

    private final Label msgLabel = new Label("Hello Checkers");
    private static AudioClip captureClip, normalClip;
    public static int FLAG_DOWN = 0, FLAG_RAISED = 1;

    private static int comboFlag = FLAG_DOWN;

    public static void setComboFlag(int code) {
        comboFlag = code;
    }

    // create root node for our demo app
    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup, msgLabel);
        msgLabel.setVisible(true);

        if (numOfRowsOccupied == 0){
            numOfRowsOccupied = HEIGHT / 2 - 1;
        }

        // fill out the board with pieces and tiles on their spots
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y < numOfRowsOccupied && (x + y) % 2 != 0) {
                    piece = makePiece(client.getPlayerRole().equalsIgnoreCase("WHITE")? BLACK : WHITE, x, y);
                }

                if (y >= WIDTH - numOfRowsOccupied && (x + y) % 2 != 0) {
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

    //determine move type (+ sieve out most of illegal moves)
    private MoveResult tryMove(Piece piece, int newX, int newY) {

        System.out.println("\ndebug: trying to move piece to:  " + newX + ", " + newY);
        System.out.println("debug: piece type: "+piece.getType().toString());
        System.out.println("debug: client role type: "+client.getPlayerRole());

        if (board[newX][newY].hasPiece()
                || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }


        if (comboFlag == FLAG_RAISED && !piece.hasComboMark()) {
            System.out.println("debug: combo-flag-raised detected");
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX()), y0 = toBoard(piece.getOldY());
        int deltaX = newX - x0, deltaY = newY - y0;
        if (deltaX == 0 || deltaY == 0)
            return new MoveResult(MoveType.NONE);
        int stepX = deltaX/Math.abs(deltaX), stepY = deltaY/Math.abs(deltaY);
        int killX, killY; //we don't know whether it is a capture move, until, board at this coordinate could be null

        if (Math.abs(deltaX) != Math.abs(deltaY)){
            return new MoveResult(MoveType.NONE);
        }

        // at this point we're dealing with a valid cross-like move
        // also we don't violate comboFlag
        if (piece.getGamemode() == REGULAR_PAWN){

            //Normal move can go only one tile "down" the board in a v-like shape
            if(Math.abs(deltaX) == -deltaY && (deltaY) == -1){ //deltaY = -1, deltaX = 1, -1
                return new MoveResult(MoveType.NORMAL);
            }

            //Kill move can go exactly two tiles in an x-like shape
            if(Math.abs(deltaY) == 2){ //deltaY = 2, -2, deltaX = 2, -2

//                killX = x0 + deltaX/2;
//                killY = y0 + deltaY/2;
                killX = newX - stepX;
                killY = newY - stepY;

                Piece capturedPiece;
                if (board[killX][killY].hasPiece()) {

                    if ((capturedPiece = board[killX][killY].getPiece()).getType() != piece.getType()){
                        return new MoveResult(MoveType.KILL, capturedPiece);
                    }
//                    return new MoveResult(MoveType.NONE);
                }
            }

        }

        if (piece.getGamemode() == Piece.KING_PAWN
                && (deltaY) != 0) {

            killX = x0;
            killY = y0;
//            int hurdles = Math.abs(deltaY);

            while (killX != newX && killY != newY){
                killX += stepX;
                killY += stepY;
                if (board[killX][killY].hasPiece()) {
                    if (board[killX][killY].getPiece().getType() != piece.getType()
                            && killX+stepX == newX
                            && killY+stepY == newY){
                        return new MoveResult(MoveType.KILL, board[killX][killY].getPiece());
                    }
                    return new MoveResult(MoveType.NONE);
                }
//                hurdles -= 1;

            }

            return new MoveResult(MoveType.NORMAL);
        }

        return new MoveResult(MoveType.NONE);
    }

    private static int toBoard(double pixel) {
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
                case NORMAL ->
                        client.pushCommand("NORMAL", x0, y0, newX, newY);
                case KILL -> {
                    Piece otherPiece = result.getPiece();
                    int killX = toBoard(otherPiece.getOldX());
                    int killY = toBoard(otherPiece.getOldY());
                    client.pushCommand("KILL", x0, y0, newX, newY, killX, killY);
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
            int finalOldX = playerRole.equalsIgnoreCase("WHITE")
                    ? oldX : invertHorizontal(oldX);
            int finalOldY = playerRole.equalsIgnoreCase("WHITE")
                    ? oldY : invertVertical(oldY);
            Platform.runLater(() -> board[finalOldX][finalOldY].getPiece().abortMove());
            return;
        }

        int newX = Integer.parseInt(commands[3]);
        int newY = Integer.parseInt(commands[4]);
        int killX = Integer.parseInt(commands[5]);
        int killY = Integer.parseInt(commands[6]);

        if (playerRole.equalsIgnoreCase("BLACK")){
            oldX = invertHorizontal(oldX);
            oldY = invertVertical(oldY);
            newX = invertHorizontal(newX);
            newY = invertVertical(newY);
            killX = invertHorizontal(killX);
            killY = invertVertical(killY);
        }

//        System.out.println("updateBoard:debug");
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

            //update view
            piece.move(finalNewX, finalNewY);

            //update logic
            board[finalNewX][finalNewY].setPiece(piece);
            board[finalOldX][finalOldY].setPiece(null);

            AudioClip clip = clip("NORMAL"); //set clip to self-move.mp3

            if ( commands[0].startsWith("KILL") ){
                Piece otherPiece = board[finalKillX][finalKillY].getPiece();

                board[finalKillX][finalKillY].setPiece(null); //update logic&view
                pieceGroup.getChildren().remove(otherPiece); //update logic

                clip = clip("CAPTURE"); //re-set clip to capture.mp3
            }

            if(clip != null){
                clip.play();
            }

            if (piece.getGamemode() == REGULAR_PAWN && !piece.hasComboMark()){
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
    }

    //does not implement capture logic for king since it is unnecessary for this app purposes
    private static boolean hasToCapture(Piece piece, Tile[][] board) {
        if(piece == null)
            return false;

        if(piece.getGamemode() == Piece.KING_PAWN)
            throw new IllegalArgumentException("don't call this method on a king piece");

        int x = toBoard(piece.getOldX()), y = toBoard(piece.getOldY());

        //search all possible directions
        int[] directions = new int[]{-1,1};
        for (int dirX : directions){
            for (int dirY : directions){
                int killX = x + dirX, killY = y + dirY;
                int newX = x + dirX * 2, newY = y + dirY * 2;

                if (newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT)
                    continue;
                //if (newX,newY) is on board then (killX,killY) is on board
//                if (killX < 0 || killX >= WIDTH || killY < 0 || killY >= HEIGHT)
//                    continue;
                if (!board[killX][killY].hasPiece())
                    continue;
                if (board[killX][killY].getPiece().getType() != piece.getType())
                    return true;
            }
        }

        return false;
    }

    public static void updateLabel(String msg, Label msgLabel){
        Platform.runLater(() -> msgLabel.setText(msg));
    }

    //adjusts game parameters for specific game types
    public static void updateGameType(String gameType) {
            switch (gameType.toUpperCase()) {
                case "CLASSIC":
                case "RUSSIAN":
                    break;
                case "POLISH":
                    WIDTH = 10;
                    HEIGHT = 10;
                    TILE_SIZE = 85;
                    break;
                default:
                    System.out.println("debug:gametype == " + gameType);
                    throw new IllegalArgumentException("invalid game type selected");
            }
    }

    private static AudioClip clip(String type){
        //lazy initialize
        if (captureClip == null) {
            captureClip = new AudioClip(Objects.requireNonNull(CheckersDemoApp.class.getResource("/music/capture.mp3")).toExternalForm());
        }
        if (normalClip == null) {
            normalClip = new AudioClip(Objects.requireNonNull(CheckersDemoApp.class.getResource("/music/move-self.mp3")).toExternalForm());
        }

        //return
        if (type.equalsIgnoreCase("CAPTURE")) {
            return captureClip;
        }
        if (type.equalsIgnoreCase("NORMAL")){
            return normalClip;
        }
        else {
            throw new IllegalArgumentException("tried to use unavailable clip");
        }
    }

    @Override
    public void init() {
        try {
            client = new CheckersClientDemo(new Socket("localhost", 4545));
            System.out.println("client connected with server");
            client.handShake();
            System.out.println("calling listener thread...");
            this.board = new Tile[WIDTH][HEIGHT];
            client.receiveMessageFromServer(board, pieceGroup, msgLabel);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to establish connection with server");
            System.exit(1);
        }
    }


    @Override
    public void start(Stage primaryStage) {

//        new ModalGamePicker(client).showAndWait();


        Scene scene = new Scene(createContent());
        primaryStage.setTitle("CheckersApp - DEMO");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}