package com.example.demo.CheckersServerDemo;


public abstract class Game {

    //reference to the Player which has to move next
    Player currentPlayer;
    AbstractPawn[][] board;
    final int boardWidth;
    final int boardHeight;

    protected Game(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        generateBoard();
        //showBoard();
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
//        return !hasPawns(PawnColor.BLACK) || !hasPawns(PawnColor.WHITE) || noMovesPossible(PawnColor.BLACK) || noMovesPossible(PawnColor.WHITE);
        return !hasPawns(PawnColor.BLACK) || !hasPawns(PawnColor.WHITE) || noMovesPossible(currentPlayer.getOpponent().playerColor);
    }

    public boolean canMove(int x, int y) {
        if (board[x][y] == null)
            return false;

        if (board[x][y] instanceof Pawn) {
            Pawn pawn = (Pawn) board[x][y];
            if (tileAvailable(x - 1, y + pawn.getDir()))
                return true;
            else if (tileAvailable(x - 2, y + 2 * pawn.getDir()) && board[x - 1][y + pawn.getDir()].getColor() != pawn.getColor()) //can throw nullpointerexception
                return true;

            if (tileAvailable(x + 1, y + pawn.getDir()))
                return true;
            else if (tileAvailable(x + 2, y + 2 * pawn.getDir()) && board[x + 1][y + pawn.getDir()].getColor() != pawn.getColor())
                return true;

        } else {
            King king = (King) board[x][y];
            int i = x - 1;
            int j = y - 1;
            while (onBoard(i,j)) { //cannot jump over multiple pawns nor same color pawns
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

    public boolean noMovesPossible(PawnColor color) {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getColor() == color && canMove(i, j))
                        return false;
                }
            }
        }
        return true;
    }

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

    public boolean tileAvailable(final int x, final int y) {
        if (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
            return (board[x][y] == null);
        }
        return false;
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
            throw new IllegalStateException("It isn't your piece");
        } else if (hasToCapture(currentPlayer) && !type.equalsIgnoreCase("KILL")) {
            throw new IllegalStateException("You must capture first!");
        } else if (!canMove(oldX,oldY)) {
            throw new IllegalStateException("This piece has no available moves!");
        }

        AbstractPawn pawnToMove = board[oldX][oldY];
        board[oldX][oldY] = null;
        board[newX][newY] = pawnToMove;

        if (type.equalsIgnoreCase("KILL")) {
            board[killX][killY] = null;
        }

        makeKingIfCond(newX, newY);
        currentPlayer = currentPlayer.getOpponent();
    }

    public void makeKingIfCond(int x, int y) {
        if (((board[x][y].getColor() == PawnColor.WHITE && y == 0) || (board[x][y].getColor() == PawnColor.BLACK && y == boardHeight - 1)) && board[x][y] instanceof Pawn)  {
            board[x][y] = new King(board[x][y].getColor());
            //System.out.println("mamy damke!");
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

    public boolean hasToCapture(int x, int y) {
        if (board[x][y] == null)
            return false;

        if (board[x][y] instanceof Pawn piece){
            int dir = piece.getDir();

            AbstractPawn capturedPiece;
            if (
                    (tileAvailable(x - 2, y + 2 * dir) && (capturedPiece = board[x - 1][y + dir]) != null)
                || (tileAvailable(x + 2, y + 2 * dir) && (capturedPiece = board[x + 1][y + dir]) != null)
            )
                if (capturedPiece.getColor() != piece.getColor())
                    return true;
        }

        if (board[x][y] instanceof King /*piece*/){
            int x1, y1;
            for (int dirX : new int[]{-1, 1}){
                for (int dirY : new int[]{-1, 1}) {
                    int step = 0;

                    do{
                        //take a step
                        step++;
                        x1 = x + dirX * step;
                        y1 = y + dirY * step;

                    } while(tileAvailable(x1, y1));
                    if (onBoard(x1,y1) && board[x1][y1].getColor() != board[x][y].getColor()){

                        //take last step
                        step++;
                        x1 = x + dirX * step;
                        y1 = y + dirY * step;

                        if (tileAvailable(x1,y1))
                            return true;
                    }
                }
            }
//            System.out.println("king does not must capture");
        }
        return false;
    }

    public boolean hasToCapture(Player player){
        for (int x = 0 ; x < boardHeight ; x++) {
            for (int y = 0 ; y < boardWidth ; y++) {
                if (board[x][y] != null && board[x][y].getColor() == player.playerColor) {
                    if (hasToCapture(x,y))
                        return true;
                }
            }
        }
        return false;
    }
}
