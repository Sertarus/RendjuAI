import core.Board;
import core.BoardPoint;
import core.Player;
import core.Stone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board clearBoard = new Board(15, 15);

    private Player black = new Player(Stone.BLACK,null);

    @Test
    public void getStonesOnBoard() {
        assertEquals(0, clearBoard.getStonesOnBoard());
    }

    @Test
    public void clear() {
        Board board = new Board(15, 15);
        board.makeTurn(0, 0);
        board.clear();
        assertEquals(clearBoard, board);
    }

    @Test
    public void get() {
        Board board = new Board(15, 15);
        board.makeTurn(0, 0);
        assertEquals(Stone.BLACK, board.get(0, 0));
        assertEquals(Stone.BLACK, board.get(new BoardPoint(0, 0)));
    }

    @Test
    public void getTurn() {
        assertEquals(Stone.BLACK, clearBoard.getTurn().getSide());
    }

    @Test
    public void setTurn() {
        Board board = new Board(15, 15);
        board.setTurn(new Player(Stone.WHITE,null));
        assertEquals(Stone.WHITE, board.getTurn().getSide());
    }

    @Test
    public void makeTurn() {
        Board board = new Board(15, 15);
        board.makeTurn(0, 0);
        assertEquals("B - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -\n" +
                "- - - - - - - - - - - - - - -", board.toString());
        board.setTurn(black);
        board.makeTurn(8, 8);
        board.setTurn(black);
        board.makeTurn(9, 9);
        board.setTurn(black);
        board.makeTurn(7, 9);
        board.setTurn(black);
        board.makeTurn(8, 10);
        board.setTurn(black);
        assertEquals(null, board.get(board.makeTurn(8, 9)));

    }

    @Test
    public void winner() {
        Board board = new Board(15, 15);
        for (int i = 0; i < 5; i++) {
            board.makeTurn(0, i);
            board.setTurn(black);
        }
        assertEquals(Stone.BLACK, board.winner());
    }

    @Test
    public void hasPossibilityOfVictoryRow() {
        Board board = new Board(15, 15);
        assertEquals(true, clearBoard.hasPossibilityOfVictoryRow());
    }
}
