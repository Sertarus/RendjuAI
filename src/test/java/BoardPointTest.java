import core.Board;
import core.BoardPoint;
import core.Stone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardPointTest {

    private BoardPoint firstBoardPoint = new BoardPoint(1, 3);
    private BoardPoint secondBoardPoint = new BoardPoint(2, 1);
    private BoardPoint firstBoardPointCopy = new BoardPoint(1, 3);

    @Test
    public void getVertical() {
        assertEquals(1, firstBoardPoint.getVertical());
    }

    @Test
    public void getHorizontal() {
        assertEquals(3, firstBoardPoint.getHorizontal());
    }

    @Test
    public void plus() {
        assertEquals(new BoardPoint(3, 4), firstBoardPoint.plus(secondBoardPoint));
    }

    @Test
    public void minus() {
        assertEquals(new BoardPoint(-1, 2), firstBoardPoint.minus(secondBoardPoint));
    }

    @Test
    public void times() {
        assertEquals(new BoardPoint(2, 6), firstBoardPoint.times(2));
    }

    @Test
    public void equals() {
        assertEquals(true, firstBoardPoint.equals(firstBoardPoint));
        assertEquals(true, firstBoardPoint.equals(firstBoardPointCopy));
        assertEquals(false, firstBoardPoint.equals(1));
    }
}
