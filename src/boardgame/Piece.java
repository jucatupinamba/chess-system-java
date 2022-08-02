package boardgame;

public class Piece {

    protected Position position;
    private Board board;

    public Piece(final Board board) {
        this.board = board;
        position = null;
    }

    protected Board getBoard() {
        return this.board;
    }
}
