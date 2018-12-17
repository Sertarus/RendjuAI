import core.*;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputerLogicTest {
    private Board board = new Board(15, 15);
    private ComputerLogic testLogic = new ComputerLogic();
    private Player player = new Player(Stone.BLACK, null);

    @Test
    public void evaluateForLastTurn() {
        board.makeTurn(7, 7);
        for (int i = 0; i < 100; i++) {
            TreeMap<Integer, BoardPoint> result = testLogic.evaluateForLastTurn(board);
            assertTrue(result.size() > 0 && result.size() < 25);
            assertTrue(result.lastKey() >= 153 && result.lastKey() <= 163);
        }
    }

    @Test
    public void makeComputerTurn() {
        board.makeTurn(0, 14);
        board.setTurn(player);
        board.makeTurn(0, 13);
        board.setTurn(player);
        board.makeTurn(0, 12);
        board.setTurn(player);
        board.makeTurn(0, 11);
        assertEquals(new BoardPoint(0, 10), testLogic.makeComputerTurn(board));
    }
}
