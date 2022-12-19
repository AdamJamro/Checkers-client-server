package com.example.demo.CheckersServerDemo;

public abstract class Game {

    //reference to the Player which has to move next
    Player currentPlayer;
    AbstractPawn[][] board;
    final int boardWidth;
    final int boardHeight;

    // grid n by m, null if empty, WHITE whilst being occupied by a white pawn, BLACK - black pawn
    //PlayerRole[][] board;
    protected Game(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        generateBoard();
    }

    private void generateBoard() {
        board = new AbstractPawn[boardHeight][boardWidth];
        for (int y = 0; y < boardHeight; y++){
            for (int x = 0; x < boardWidth; x++){
                if (y <= 2 && (x+y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.BLACK); //PlayerRole.BLACK;
                }
                if (y >= boardHeight-3 && (x+y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.WHITE); //PlayerRole.WHITE;
                }
                //System.out.println("board[" + x + "][" + y +"] =  " + board[x][y]);
            }
        }
    }

    private void showBoard() {
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (board[x][y] != null) {
                    if (board[x][y].getColor() == PawnColor.WHITE)
                        System.out.print('W');
                    else System.out.print('B');
                }
                else System.out.print(' ');
            }
            System.out.println();
        }
    }

    public boolean hasWinner() {
        return !hasPawns(PawnColor.BLACK) || !hasPawns(PawnColor.WHITE); // || noMovesPossible(PawnColor.BLACK) || noMovesPossible(PawnColor.WHITE);
    }

    public abstract boolean canMove(int x, int y);

    public abstract boolean noMovesPossible(PawnColor color);

    public boolean hasPawns(PawnColor color) {
        for (int x = 0 ; x < boardHeight ; x++) {
            for (int y = 0 ; y < boardWidth ; y++) {
                if (board[x][y] != null) {
                    if (board[x][y].getColor() == color)
                        return true;
                }
            }
        }
        return false;
    }

    public boolean onBoard(int x, int y) {
        return x < boardHeight && x >= 0 && y < boardWidth && y >= 0;
    }

    public synchronized void move(String type, int oldX, int oldY, int newX, int newY, int killX, int killY, Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[newX][newY] != null) {
            throw new IllegalStateException("Cell already occupied");
        } else if (newX >= boardWidth || newY >= boardHeight) {
            throw new IllegalStateException("Pawns cannot leave the board");
        } else if (board[oldX][oldY].getColor() != currentPlayer.playerColor) {
            throw new IllegalStateException("Not your color");
        }

        AbstractPawn pawnToMove = board[oldX][oldY];
        board[oldX][oldY] = null;
        board[newX][newY] = pawnToMove; //currentPlayer.playerColor;

        if (type.equalsIgnoreCase("KILL")) {
            board[killX][killY] = null;
        }

        currentPlayer = currentPlayer.getOpponent();
    }

    public void turnIntoKing(int x, int y) {
        if (board[x][y] == null || !(board[x][y] instanceof Pawn)) {
            throw new IllegalStateException("Something is wrong");
        }
        if ((board[x][y].getColor() == PawnColor.WHITE && y == 0) || (board[x][y].getColor() == PawnColor.BLACK && y == boardHeight-1)) {
            board[x][y] = new King(board[x][y].getColor());
        }
    }
}
