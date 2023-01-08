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

    public void generateBoard() {
        board = new AbstractPawn[boardHeight][boardWidth];
        int numOfOccupiedRows = (boardHeight-2)/2;
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (y < numOfOccupiedRows && (x + y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.BLACK);
                }
                if (y >= boardHeight - numOfOccupiedRows && (x + y) % 2 != 0) {
                    board[x][y] = new Pawn(PawnColor.WHITE);
                }
            }
        }
    }

    public void showBoard() {
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

        if (hasToCapture(x,y))
           return true;

        int[] dirX = new int[]{-1, 1}, dirY;

        if (board[x][y] instanceof Pawn pawn)
            dirY = new int[]{pawn.getDir()};
        else
            dirY = new int[]{-1, 1};

        for (int i : dirX){
            for (int j : dirY){
                if (tileAvailable(x + i, y + j))
                    return true;
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

    public synchronized void move(String type,
                                  int oldX, int oldY,
                                  int newX, int newY,
                                  int killX, int killY,
                                  Player player) {

        throwExceptionWhenLogicBroken(type, oldX, oldY, newX, newY, killX, killY, player);

        AbstractPawn pawnToMove = board[oldX][oldY];
        board[oldX][oldY] = null;
        board[newX][newY] = pawnToMove;

        if (type.equalsIgnoreCase("KILL")) {
            board[killX][killY] = null;

            makeKingIfCond(newX, newY);
        }

        currentPlayer = currentPlayer.getOpponent();
    }


    protected void throwExceptionWhenLogicBroken(String type, int oldX, int oldY,
                                                 int newX, int newY,
                                                 int killX, int killY,
                                                 Player player)
            throws IllegalStateException {
        if (player != currentPlayer)
            throw new IllegalStateException("Not your turn");
        if (player.getOpponent() == null)
            throw new IllegalStateException("You don't have an opponent yet");
        if (board[newX][newY] != null)
            throw new IllegalStateException("Cell already occupied");
        if(newX >= boardWidth || newY >= boardHeight)
            throw new IllegalStateException("Pawns cannot leave the board");
        if (board[oldX][oldY].getColor() != currentPlayer.playerColor)
            throw new IllegalStateException("It isn't your piece");
        if (!isValidMove(type, oldX, oldY, newX, newY, killX, killY))
            throw new IllegalStateException("illegal move!");
        if (hasToCapture(currentPlayer) && !type.equalsIgnoreCase("KILL"))
            throw new IllegalStateException("You must capture first!");
        if (assessMove(type, oldX, oldY, newX, newY) < bestMoveValue(currentPlayer))
            throw new IllegalStateException("Unluckily 4 u, every player must play their best move!");
        System.gc();
    }

    private int bestMoveValue(Player player) {
        int value = 0;
        for (int x = 0 ; x < boardHeight ; x++) {
            for (int y = 0 ; y < boardWidth ; y++) {
                if (board[x][y] != null && board[x][y].getColor() == player.playerColor) {
                    var tmpValue = assessAvailableMoves(x,y);
                    if (tmpValue > value)
                        value = tmpValue;
                }
            }
        }
        return value;
    }




    public void makeKingIfCond(int x, int y) {
        if (!(board[x][y] instanceof Pawn))
            return;

        if (hasToCapture(x,y))
            return;

        if (((board[x][y].getColor() == PawnColor.WHITE && y == 0) || (board[x][y].getColor() == PawnColor.BLACK && y == boardHeight - 1)))  {
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
//            int dir = piece.getDir(); //if need to implement only forward captures use dir the attribute

            //search all possible directions
            AbstractPawn capturedPiece;
            int[] directions = new int[]{-1,1};
            for (int dirX : directions){
                for (int dirY : directions){
                    if (!tileAvailable(x + 2 * dirX, y + 2 * dirY))
                        continue;
                    if ((capturedPiece = board[x + dirX][y + dirY]) == null)
                        continue;
                    if (capturedPiece.getColor().getOpposite() == piece.getColor())
                        return true;
                }
            }
        }

        if (board[x][y] instanceof King /*piece*/){
            int x1, y1;
            int[] directions = new int[]{-1, 1};
            for (int dirX : directions){
                for (int dirY : directions) {
                    int step = 0;

                    do{
                        //take a step
                        step++;
                        x1 = x + dirX * step;
                        y1 = y + dirY * step;

                    } while(tileAvailable(x1, y1));
                    //board[x1][y1] is the first hurdle encountered
                    //...or end of board!

                    if (!onBoard(x1,y1))
                        return false;

                    if (board[x1][y1].getColor().getOpposite() == board[x][y].getColor()){

                        //take last step (to pass the hurdle)
                        step++;
                        x1 = x + dirX * step;
                        y1 = y + dirY * step;

                        if (!tileAvailable(x1,y1))
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

    private boolean isValidMove(String type, int oldX, int oldY, int newX, int newY, AbstractPawn pieceToMove){
        final int deltaX = newX - oldX, deltaY = newY - oldY;
        final int dx = deltaX/Math.abs(deltaX), dy = deltaY/Math.abs(deltaY);
        final int killX = newX - dx, killY = newY - dy;
        return isValidMove(type, oldX, oldY, newX, newY, killX, killY, pieceToMove);
    }

    private boolean isValidMove(String type, int oldX, int oldY, int newX, int newY, int killX, int killY){
        return isValidMove(type, oldX, oldY, newX, newY, killX, killY, board[oldX][oldY]);
    }

    private boolean isValidMove(String type, int oldX, int oldY, int newX, int newY, int killX, int killY, AbstractPawn pieceToMove){
        final int deltaX = newX - oldX, deltaY = newY - oldY;
        final int dx = deltaX/Math.abs(deltaX), dy = deltaY/Math.abs(deltaY);
        int numOfHurdles = 0, tmpX = oldX + dx, tmpY = oldY + dy;


        if(!tileAvailable(newX,newY))
            return false;

        if(board[newX][newY] != null)
            return false;
        if(Math.abs(deltaX) != Math.abs(deltaY))
            return false;
        if(deltaX == 0 || deltaY == 0)
            return false;

        while(tmpX != newX && tmpY != newY){
            if (board[tmpX][tmpY] != null){
                numOfHurdles ++;
            }
            tmpX += dx;
            tmpY += dy;
        }

        if (type.equalsIgnoreCase("NORMAL")){
            if(numOfHurdles != 0)
                return false;
        }
        if (type.equalsIgnoreCase("KILL")) {
            if(!onBoard(killX,killY))
                return false;
            if(board[killX][killY] == null)
                return false;
            if(!(killX + dx == newX && killY + dy == newY))
                return false;
            if(pieceToMove.getColor().getOpposite() != board[killX][killY].getColor())
                return false;
            if(numOfHurdles != 1)
                return false;
            if(Math.abs(deltaX) == 1 || Math.abs(deltaY) == 1)
                return false;
        }//assert:
        //piece attempts to move in a cross like manner to a spot that isn't occupied by any piece
        //if it is a normal move (no captures) there are no hurdles that it passes (of any color)
        //if it is a capture move there is exactly one hurdle that he passes (of an opposite color)
        if(pieceToMove instanceof Pawn piece){

            if(type.equalsIgnoreCase("NORMAL")){
                //checks both if dy is non-0 and if directory is incorrect
                if(dy * piece.getDir() <= 0)
                    return false;
            }

            if(type.equalsIgnoreCase("KILL")){
                //checks if we skipped exactly one tile
                if(Math.abs(deltaX) != 2)
                    return false;
            }
        }

        //archetype for different game-types
        if(pieceToMove instanceof King piece){
            throw new IllegalStateException("NOT IMPLEMENTED YET!");
        }

        return true;
    }



    private int assessAvailableMoves(int x, int y) {
        int maxMoveValue = 0;
        int tmpMoveValue = 0;
        if(hasToCapture(x,y)) {

            if (board[x][y] instanceof Pawn piece) {

                for (int i : new int[]{-1, 1}) {
                    for (int j : new int[]{-1, 1}) {
                        int oldX = x, oldY = y, newX = x + i * 2, newY = y + j * 2, killX = x + i, killY = y + j;

                        if (isValidMove("KILL", oldX, oldY, newX, newY, killX, killY, piece))
                            tmpMoveValue = assessMove("KILL", oldX, oldY, newX, newY);

                    maxMoveValue = Math.max(maxMoveValue, tmpMoveValue);
                    }
                }
            }

            if (board[x][y] instanceof King piece){
                throw new IllegalStateException("NOT IMPLEMENTED YET");
            }
        }
        return maxMoveValue;
    }

    //returns int k:
    // k = 0 -> move is normal (does not capture)
    // k > 0 -> total amount of points given by this recipe:
    // gain +1 for every capture,
    // gain +2 for king captures
    // assert move is valid before this method call!
    private int assessMove(String type, int oldX, int oldY, int newX, int newY) {
        return assessMove(type, oldX, oldY, newX, newY, new String[boardWidth][boardHeight], board[oldX][oldY]);
    }


    // usedBoard is a copy of the board posing as a mark table for excluded tiles, the ones we "captured" with the search
    // as the method looks and counts the best combo moves it needs to somehow (and that's how) keep in mind which tiles
    // are already covered in a branch's path (supposedly already captured prior in a particular path)
    // if an element is null it is free to be used - we do not skip, if it is a string - we do to skip it.
    private int assessMove(String type, int oldX, int oldY, int newX, int newY, String[][] usedBoard, AbstractPawn pieceToMove) {
        //throw new IllegalStateException("NOT IMPLEMENTED YET");
        final int deltaX = newX - oldX, deltaY = newY - oldY;
        final int dx = deltaX/Math.abs(deltaX), dy = deltaY/Math.abs(deltaY);
        final int killX = newX - dx, killY = newY - dy;

        usedBoard[killX][killY] = "killed";

        System.out.println("debug:assesMove():" +
                oldX + ":" + oldY + ":" + newX + ":" + newY + ":" + killX + ":" + killY );

        int moveValue = 0;

        if (type.equalsIgnoreCase("NORMAL")) {
            return 0;
        }

        //if it is a capture check every possible combo-capture by another four assesMove() calls (for each direction)
        //choose best of the recurring branches and make moveValue be their total sum
        if (type.equalsIgnoreCase("KILL")) {

            if(pieceToMove instanceof Pawn piece){

                moveValue ++;

                //bonus for capturing a king
                if (board[killX][killY] instanceof King){
                    moveValue ++;
                }
                int[][] tmpMoveValue = new int[][]{{moveValue, moveValue}, {moveValue, moveValue}};

                //checks every of the four directions (i,j): (1,1),(-1,1),(1,-1),(-1,-1)
                for (int i : new int[]{-1,1}){
                    for (int j : new int[]{-1,1}){

                        if (!tileAvailable(newX + i * 2, newY + j * 2)){
                            continue;
                        }
                        if (usedBoard[newX + i][newY + j] != null){
                            continue;
                        }

                        if(isValidMove(type, newX, newY, newX + i * 2, newY + j * 2, piece)) {
                            tmpMoveValue[i==1?1:0][j==1?1:0] += assessMove(type, newX, newY, newX + i * 2, newY + j * 2, usedBoard, piece);
                        }
                    }
                }

                //pick best move
//                moveValue += Arrays.stream(new int[]{
//                    tmpMoveValue[0][0],
//                    tmpMoveValue[0][1],
//                    tmpMoveValue[1][0],
//                    tmpMoveValue[1][1],
//                }).max().getAsInt();

                moveValue += Math.max(
                        Math.max(tmpMoveValue[0][0],
                        tmpMoveValue[0][1]),
                        Math.max(tmpMoveValue[1][0],
                        tmpMoveValue[1][1]));

            }

            if(pieceToMove instanceof King piece){
                throw new IllegalStateException("NOT IMPLEMENTED YET!");
            }
        }



        return moveValue;
    }

}
