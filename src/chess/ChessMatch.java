package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private void initialSetup(){

        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

    }

    private Board board;
    private int turn;
    private Color currentPlayer;

    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch(){
        board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }
    public Color getCurrentPlayer(){
        return currentPlayer;
    }
    public int getTurn(){
        return turn;
    }

    public boolean getCheck(){
        return check;
    }
    public boolean getCheckMate(){
        return checkMate;
    }

   public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
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

    public boolean [][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source =  sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("Você não pode se colocar em Xeque!");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        check = (testCheck(oponent(currentPlayer))) ? true : false;

        if(testCheckMate(oponent(currentPlayer))){
            checkMate = true;
        }
        else {
            nextTurn();
        }

        //#specialmove en passant
        if(movedPiece instanceof  Pawn && (target.getRow()) == source.getRow() - 2 ||
                target.getRow() == source.getRow() + 2){
            enPassantVulnerable = movedPiece;
        }
        else{
            enPassantVulnerable = null;
        }
        return (ChessPiece)capturedPiece;
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if(capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        //#special move castling kingside rook
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() + 3);
            Position targetTorre = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceTorre);
            board.placePiece(rook, targetTorre);
            rook.increaseMoveCount();
        }

        //#special move castling queenside rook
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        //#spcialmove en passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                }
                else{
                    pawnPosition = new Position(target.getRow() -1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }
        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);
        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        //#special move castling kingside rook
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() + 3);
            Position targetTorre = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetTorre);
            board.placePiece(rook, sourceTorre);
            rook.decreaseMoveCount();
        }

        //#special move castling queenside rook
        if(p instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceTorre = new Position(source.getRow(), source.getColumn() - 4);
            Position targetTorre = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetTorre);
            board.placePiece(rook, sourceTorre);
            rook.decreaseMoveCount();
        }

        //#spcialmove en passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                Position pawnPosition;
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                }
                else{
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }
    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException(("Não há peça na posição selecionada"));
        }
        if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("A peça escolhida não é sua.");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("Não existe movimentos possíveis para a peça escolihda.");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            throw new ChessException("A peça escolhida não pode se mover para a porição de destino.");
        }
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private Color oponent(Color color){
        return (color == Color.WHITE)? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color){
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x)
                        .getColor() == color)
                        .collect(Collectors.toList());
        for(Piece p : list){
            if(p instanceof King){
                return(ChessPiece) p;
            }
        }
        throw new IllegalStateException("Não existe o rei da cor " + color + " no tabuleiro." );
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> oponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x)
                        .getColor() == oponent(color))
                        .collect(Collectors.toList());
        for(Piece p : oponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color){
        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x)
                        .getColor() == color)
                        .collect(Collectors.toList());
        for(Piece p : list){
            boolean [][] mat = p.possibleMoves();
            for(int i = 0; i< board.getRows(); i++){
                for(int t = 0; t<board.getColumns(); t++){
                    if(mat[i][t]){
                       Position source = ((ChessPiece)p).getChessPosition().toPosition();
                       Position target = new Position(i,t);
                       Piece cccapturedPiece = makeMove(source, target);
                       boolean testCheck = testCheck(color);
                       undoMove(source, target, cccapturedPiece);
                       if(!testCheck){
                           return false;
                       }
                    }
                }
            }
        }
        return true;
    }
    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
}
