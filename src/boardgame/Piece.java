package boardgame;

public abstract class Piece {

    protected Position position;
    private Board board;

    public Piece(final Board board) {
        this.board = board;
        position = null;
    }

    protected Board getBoard() {
        return this.board;
    }

    public abstract boolean[][] possibleMoves();

    public boolean possibleMove(Position position){
        return possibleMoves()[position.getRow()][position.getColumn()];
    }

    public boolean isThereAnyPossibleMove(){
        boolean[][] mat = possibleMoves();
        for(int i =0; i<mat.length; i++){
            for(int t = 0; t< mat.length; t++){
                if(mat[i][t]){
                    return true;
                }
            }
        }
        return false;
    }
}
