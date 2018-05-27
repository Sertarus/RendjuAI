package core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Board {
    private static final int LENGTH_TO_WIN = 5;

    private final int width;

    private final int height;

    private int stonesOnBoard = 0;

    @NotNull
    private final Map<BoardPoint, Stone> stones = new HashMap<>();

    @NotNull
    private Stone turn = Stone.BLACK;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        for (int vertical = 0; vertical < width; vertical++) {
            for (int horizontal = 0; horizontal < height; horizontal++) {
                stones.put(new BoardPoint(vertical, horizontal), null);
            }
        }
    }

    public int getStonesOnBoard() {
        return stonesOnBoard;
    }

    public void clear() {
        for (BoardPoint boardPoint : stones.keySet()) {
            stones.put(boardPoint, null);
        }
        stonesOnBoard = 0;
        turn = Stone.BLACK;
    }

    @Nullable
    public Stone get(int vertical, int horizontal) {
        return stones.get(new BoardPoint(vertical, horizontal));
    }

    @Nullable
    public Stone get(BoardPoint boardPoint) {
        return stones.get(boardPoint);
    }

    @NotNull
    public Stone getTurn() {
        return turn;
    }

    public void setTurn(@NotNull Stone turn) {
        this.turn = turn;
    }

    public BoardPoint makeTurn(int vertical, int horizontal) {
        BoardPoint boardPoint = new BoardPoint(vertical, horizontal);
        if (vertical < 0 || vertical >= width || horizontal < 0 || horizontal >= height) return null;
        if (stones.get(boardPoint) == null && (getTurn() == Stone.WHITE || isPlayable(boardPoint))) {
            stones.put(boardPoint, turn);
            stonesOnBoard++;
            turn = turn.opposite();
            return boardPoint;
        }
        return null;
    }

    static private final BoardPoint[] DIRECTIONS = new BoardPoint[]{
            new BoardPoint(0, 1),
            new BoardPoint(1, 0),
            new BoardPoint(1, 1),
            new BoardPoint(-1, 1),
            new BoardPoint(0, -1),
            new BoardPoint(-1, 0),
            new BoardPoint(-1, -1),
            new BoardPoint(1, -1)
    };

    private Stone findRow(boolean countEmptyPoints) {
        for (BoardPoint boardPoint : stones.keySet()) {
            Stone startStone = stones.get(boardPoint);
            if (startStone == null && !countEmptyPoints) continue;
            for (int i = 0; i < 4; i++) {
                BoardPoint current = new BoardPoint(boardPoint.getVertical(), boardPoint.getHorizontal());
                int length = 1;
                for (; length < LENGTH_TO_WIN; length++) {
                    current = current.plus(DIRECTIONS[i]);
                    if (startStone == null && get(current) != null) {
                        startStone = get(current);
                    }
                    if (!stones.containsKey(current) ||
                            (startStone != null && countEmptyPoints && stones.get(current) != startStone) ||
                            (!countEmptyPoints && stones.get(current) != startStone))
                        break;
                }
                if (length == LENGTH_TO_WIN && startStone != null) return startStone;
                else if (length == LENGTH_TO_WIN) return Stone.BLACK;
            }
        }
        return null;
    }

    public boolean hasPossibilityOfVictoryRow() {
        return findRow(true) != null;
    }

    public Stone winner() {
        return findRow(false);
    }

    private boolean isPlayable(BoardPoint boardPoint) {
        return findPotentialRow(6, boardPoint) == 0 && findPotentialRow(3, boardPoint) <= 1 &&
                findPotentialRow(4, boardPoint) <= 1;
    }

    @Nullable
    private List<BoardPoint> checkRowInDirection(BoardPoint direction, int directionCoefficient,
                                                 BoardPoint startPoint, int length) {
        BoardPoint currentPoint = startPoint.minus(direction.times(directionCoefficient));
        int numberOfStones = 0;
        List<BoardPoint> row = new ArrayList<>();
        for (int i = 0; i <= length; i++) {
            if (get(currentPoint) == Stone.BLACK) numberOfStones++;
            else if (get(currentPoint) == Stone.WHITE) break;
            row.add(currentPoint);
            currentPoint = currentPoint.plus(direction);
        }
        if (numberOfStones == length - 1) {
            if (stones.get(row.get(row.size() - 1)) == null && !row.get(row.size() - 1).equals(startPoint))
                row.remove(row.size() - 1);
            if (stones.get(row.get(0)) == null && !row.get(0).equals(startPoint)) row.remove(0);
            return row;

        }
        return null;
    }

    private double findPotentialRow(int length, BoardPoint boardPoint) {
        List<Integer> unnecessaryDirections = new ArrayList<>();
        double numberOfDetectedRows = 0;
        for (int i = 0; i <= 7; i++) {
            if (unnecessaryDirections.contains(i)) continue;
            for (int j = 1; j <= length; j++) {
                List<BoardPoint> row = checkRowInDirection(DIRECTIONS[i], j, boardPoint, length);
                if (row != null) {
                    unnecessaryDirections.add(i + 4);
                    BoardPoint linearCoefficient = row.get(1).minus(row.get(0));
                    BoardPoint[] possibleObstacles = new BoardPoint[]{
                            row.get(0).minus(linearCoefficient),
                            row.get(row.size() - 1).plus(linearCoefficient),
                            row.get(0).minus(linearCoefficient.times(2)),
                            row.get(row.size() - 1).plus(linearCoefficient.times(2)),
                    };
                    boolean[] emptyPoints = new boolean[]{
                            true,
                            true,
                            true,
                            true
                    };
                    for (int k = 0; k <= 3; k++) {
                        if ((get(possibleObstacles[k]) != null || !stones.containsKey(possibleObstacles[k]))
                                && length == 3) {
                            emptyPoints[k] = false;
                        }
                    }
                    boolean twoRowsOfFourInSevenCells = (length == 4 && row.size() == 5 &&
                            (get(possibleObstacles[2]) == Stone.BLACK || get(possibleObstacles[3]) == Stone.BLACK));
                    if (length == 4 && ((blockedPoint(possibleObstacles[2]) &&
                            (blockedPoint(possibleObstacles[3]) || blockedPoint(possibleObstacles[1]))) ||
                            (blockedPoint(possibleObstacles[3]) && (blockedPoint(possibleObstacles[2]) ||
                                    blockedPoint(possibleObstacles[0])))))
                        numberOfDetectedRows--;
                    if ((emptyPoints[0] && emptyPoints[1]) && (emptyPoints[2] || emptyPoints[3] || row.size() == 4))
                        numberOfDetectedRows++;
                    if (row.size() == 3 && (!emptyPoints[2] && emptyPoints[3]) || (emptyPoints[2] && !emptyPoints[3]))
                        numberOfDetectedRows = numberOfDetectedRows - 0.5;
                    if (twoRowsOfFourInSevenCells) numberOfDetectedRows++;
                    break;
                }
            }
        }
        return numberOfDetectedRows;
    }

    private boolean blockedPoint(BoardPoint boardPoint) {
        return get(boardPoint) != null || !stones.containsKey(boardPoint);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Board) {
            Board other = (Board) obj;
            return width == other.width && height == other.height && stones.equals(other.stones);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 17 * result + width;
        result = result * 7 + height;
        return result + stones.hashCode() + 23 * (stonesOnBoard + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Stone stone = get(i, j);
                if (stone == null) {
                    if (j != 14) sb.append("- ");
                    else sb.append("-");
                    continue;
                }
                switch (stone) {
                    case BLACK:
                        sb.append("B ");
                        break;
                    case WHITE:
                        sb.append("W ");
                        break;
                }
            }
            if (i != 14) sb.append("\n");
        }
        return sb.toString();
    }
}
