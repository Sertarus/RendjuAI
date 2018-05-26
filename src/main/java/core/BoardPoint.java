package core;

public final class BoardPoint {
    private final int vertical;

    private final int horizontal;

    public BoardPoint(int vertical, int horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public int getVertical() {
        return vertical;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public BoardPoint plus(BoardPoint other) {
        return new BoardPoint(vertical + other.vertical, horizontal + other.horizontal);
    }

    public BoardPoint minus(BoardPoint other) {
        return new BoardPoint(vertical - other.vertical, horizontal - other.horizontal);
    }

    public BoardPoint times(int other) {
        return new BoardPoint(vertical * other, horizontal * other);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof BoardPoint) {
            BoardPoint boardPoint = (BoardPoint) other;
            return vertical == boardPoint.vertical && horizontal == boardPoint.horizontal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 17 * result + vertical;
        return 37 * result + horizontal;
    }

    @Override
    public String toString() {
        return "" + vertical + horizontal;
    }
}
