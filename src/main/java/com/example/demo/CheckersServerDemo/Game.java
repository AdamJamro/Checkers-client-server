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
        showBoard();
    }

    public abstract void generateBoard();

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

    public boolean canMove(int x, int y) {
        if (board[x][y] == null)
            return false;

        if (board[x][y] instanceof Pawn) {
            Pawn pawn = (Pawn) board[x][y];
            if (onBoard(x - 1, y + pawn.getDir())) {
                if (board[x - 1][pawn.getDir()] == null)
                    return true;
                else if (onBoard(x - 2, y + 2 * pawn.getDir())) {
                    if (board[x - 2][y + 2 * pawn.getDir()] == null)
                        return true;
                }
            }
            if (onBoard(x + 1, y + pawn.getDir())) {
                if (board[x + 1][y + pawn.getDir()] == null)
                    return true;
                else if (onBoard(x + 2, y + 2 * pawn.getDir())) {
                    if (board[x + 2][y + 2 * pawn.getDir()] == null)
                        return true;
                }
            }
        } else { //King
            King king = (King) board[x][y];
            int i = x - 1;
            int j = y - 1;
            while (onBoard(i,j)) {
                if (board[i][j] == null)
                    return true;
                i--;
                j--;
            }
            i = x + 1;
            j = y - 1;
            while (onBoard(i,j)) {
                if (board[i][j] == null)
                    return true;
                i++;
                j--;
            }
            i = x + 1;
            j = y + 1;
            while (onBoard(i,j)) {
                if (board[i][j] == null)
                    return true;
                i++;
                j++;
            }
            i = x - 1;
            j = y + 1;
            while (onBoard(i,j)) {
                if (board[i][j] == null)
                    return true;
                i--;
                j++;
            }
        }
        return false;
    }

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

    public AbstractPawn getPawn(int x, int y) {
        if (onBoard(x,y))
            return board[x][y];
        else return null;
    }

    public void setPawn(int x, int y, AbstractPawn abstractPawn) {
        if (onBoard(x,y))
            board[x][y] = abstractPawn;
    }
}
