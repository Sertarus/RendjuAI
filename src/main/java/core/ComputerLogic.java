package core;


import java.util.*;

public class ComputerLogic {

    private static HashMap<String, Integer> rowPatterns = new HashMap<>();

    static {
        rowPatterns.put("SSSSS", 10000);
        rowPatterns.put("_SSSS_", 2000);
        rowPatterns.put("SSSS_", 1100);
        rowPatterns.put("SSS_S", 650);
        rowPatterns.put("SS_SS", 650);
        rowPatterns.put("SSS__", 250);
        rowPatterns.put("_SSS_", 330);
        rowPatterns.put("SS_S_", 180);
        rowPatterns.put("SS__S", 180);
        rowPatterns.put("SS___", 70);
        rowPatterns.put("_SS__", 100);
        rowPatterns.put("__SSS__", 780);
    }

    public BoardPoint makeComputerTurn(Board board) {
        TreeMap<Integer, BoardPoint> potentialTurns = evaluateForLastTurn(board);
        if (potentialTurns.isEmpty() && board.get(7, 7) == null) board.makeTurn(7, 7);
        else {

            BoardPoint bestTurn = potentialTurns.get(potentialTurns.lastKey());
            while (board.get(bestTurn) != null ||
                    (board.getTurn().getSide() == Stone.BLACK && !board.isPlayable(bestTurn))) {
                potentialTurns.remove(potentialTurns.lastKey());
                bestTurn = potentialTurns.get(potentialTurns.lastKey());
            }
            if (board.getTurn().getSide() == Stone.BLACK) {
                while (!board.isPlayable(potentialTurns.get(potentialTurns.lastKey()))) {
                    potentialTurns.remove(potentialTurns.lastKey());
                }
            }
            board.makeTurn(bestTurn.getVertical(), bestTurn.getHorizontal());
            potentialTurns.remove(potentialTurns.lastKey());
            return bestTurn;
        }
        return new BoardPoint(7, 7);
    }

    public Integer evaluateTurn(Board board, BoardPoint boardPoint, Stone stone) {
        StringBuilder verticalRow = new StringBuilder();
        for (int i = -4; i <= 4; i++) {
            if (boardPoint.getHorizontal() + i < 0 || boardPoint.getHorizontal() + i > 15) continue;
            if (board.get(boardPoint.getVertical(), boardPoint.getHorizontal() + i) == stone || i == 0)
                verticalRow.append("S");
            else if (board.get(boardPoint.getVertical(), boardPoint.getHorizontal() + i) == stone.opposite())
                verticalRow.append("O");
            else if (board.get(boardPoint.getVertical(), boardPoint.getHorizontal() + i) == null)
                verticalRow.append("_");
        }
        StringBuilder horizontalRow = new StringBuilder();
        for (int i = -4; i <= 4; i++) {
            if (boardPoint.getVertical() + i < 0 || boardPoint.getVertical() + i > 15) continue;
            if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal()) == stone || i == 0)
                horizontalRow.append("S");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal()) == stone.opposite())
                horizontalRow.append("O");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal()) == null)
                horizontalRow.append("_");
        }
        StringBuilder firstDiagRow = new StringBuilder();
        for (int i = -4; i <= 4; i++) {
            if (boardPoint.getVertical() + i < 0 || boardPoint.getVertical() + i > 15) continue;
            if (boardPoint.getHorizontal() + i < 0 || boardPoint.getHorizontal() + i > 15) continue;
            if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() + i) == stone || i == 0)
                firstDiagRow.append("S");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() + i) == stone.opposite())
                firstDiagRow.append("O");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() + i) == null)
                firstDiagRow.append("_");
        }
        StringBuilder secondDiagRow = new StringBuilder();
        for (int i = -4; i <= 4; i++) {
            if (boardPoint.getVertical() + i < 0 || boardPoint.getVertical() + i > 15) continue;
            if (boardPoint.getHorizontal() - i < 0 || boardPoint.getHorizontal() - i > 15) continue;
            if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() - i) == stone || i == 0)
                secondDiagRow.append("S");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() - i) == stone.opposite())
                secondDiagRow.append("O");
            else if (board.get(boardPoint.getVertical() + i, boardPoint.getHorizontal() - i) == null)
                secondDiagRow.append("_");
        }
        String[] rows = new String[]{
                verticalRow.toString(),
                horizontalRow.toString(),
                firstDiagRow.toString(),
                secondDiagRow.toString()
        };
        Integer value = new Random().nextInt(5);
        for (String row : rows) {
            for (String rowPattern : rowPatterns.keySet()) {
                if (row.contains(rowPattern) || row.contains(new StringBuilder(rowPattern).reverse())) {
                    value += rowPatterns.get(rowPattern);
                    if (stone != board.getTurn().getSide()) value -= rowPatterns.get(rowPattern) / 10;
                }
            }
        }
        return value;
    }

    public TreeMap<Integer, BoardPoint> evaluateForLastTurn(Board board) {
        TreeMap<Integer, BoardPoint> potentialTurns = new TreeMap<>();
        ArrayList<BoardPoint> evaluatedTurns = new ArrayList<>();
        for (BoardPoint playedPoint : board.getPlayedCells()) {
            for (int i = playedPoint.getVertical() - 2; i <= playedPoint.getVertical() + 2; i++) {
                for (int j = playedPoint.getHorizontal() - 2; j <= playedPoint.getHorizontal() + 2; j++) {
                    if (i >= 0 && i < 15 && j >= 0 && j < 15 && board.get(i, j) == null &&
                            !evaluatedTurns.contains(new BoardPoint(i, j))) {
                        BoardPoint currentPoint = new BoardPoint(i, j);
                        int offensiveTurnValue = evaluateTurn(board, currentPoint, board.getTurn().getSide());
                        int defensiveTurnValue = evaluateTurn(board, currentPoint, board.getTurn().getSide().opposite());
                        potentialTurns.put(offensiveTurnValue + defensiveTurnValue, currentPoint);
                        evaluatedTurns.add(currentPoint);
                    }
                }
            }
        }
        return potentialTurns;
    }
}

