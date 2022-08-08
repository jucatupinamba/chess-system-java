package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece [][] pieces;

    public Board(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(final int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(final int columns) {
        this.columns = columns;
    }

    public Piece pieces(int row, int column){
        return pieces [row][column];
    }

    public Piece piece(Position position){
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position){
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }
}
