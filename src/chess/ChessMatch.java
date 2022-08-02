package chess;

import boardgame.Board;

public class ChessMatch {

    private Board board;

    public ChessMatch(){
        board = new Board(8,8);
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++){
            for(int c = 0; c < board.getColumns(); c++){
                mat[i][c] = (ChessPiece) board.pieces(i,c);
            }
        }
        return mat;
    }
}
