package boardgame;

public class Board {

    private int row;
    private int columns;
    private Piece [][] pieces;

    public Board(final int row, final int columns) {
        this.row = row;
        this.columns = columns;
        pieces = new Piece[row][columns];
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(final int columns) {
        this.columns = columns;
    }
}
