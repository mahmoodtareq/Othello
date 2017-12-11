/**
 * Created by HP on 08-Dec-17.
 */
public class Move {
    public int row;
    public int col;

    public Move(int row, int col) {
        this.col = col;
        this.row = row;
    }

    @Override
    public int hashCode() {
        int result = row * col;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Move other = (Move) obj;
        return row == other.row && col == other.col;
    }
}
