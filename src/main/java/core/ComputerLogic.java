package core;


import java.util.*;

public class ComputerLogic {
    private TreeMap<Integer, BoardPoint> potentialTurns = new TreeMap<>();

    private static final StringBuilder[] rowPattern = new StringBuilder[]{
            new StringBuilder("SSSSS"),
            new StringBuilder("_SSSS_"),
            new StringBuilder("SSSS_"),
            new StringBuilder("SSS_S"),
            new StringBuilder("SS_SS"),
            new StringBuilder("SSS__"),
            new StringBuilder("_SSS_"),
            new StringBuilder("SS_S_"),
            new StringBuilder("SS__S"),
            new StringBuilder("SS___"),
            new StringBuilder("_SS__"),
            new StringBuilder("__SSS__")
    };

    public BoardPoint makeComputerTurn(Board board, List<BoardPoint> playedCells) {
        if (potentialTurns.isEmpty() && board.get(7, 7) == null) board.makeTurn(7, 7);
        else {
            evaluateForLastTurn(board, playedCells);
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
        return board.findPoint(7, 7);
    }

    public void clearPotentialTurns() {
        potentialTurns.clear();
    }

    private Integer evaluateTurn(Board board, BoardPoint boardPoint, Stone stone) {
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
            if (row.contains(rowPattern[0])) value += 10000;
            if (row.contains(rowPattern[1]) || row.contains(rowPattern[1].reverse())) value += 2000;
            if (row.contains(rowPattern[2]) || row.contains(rowPattern[2].reverse())) value += 1100;
            if (row.contains(rowPattern[3]) || row.contains(rowPattern[3].reverse()) ||
                    row.contains(rowPattern[4]) || row.contains(rowPattern[4].reverse())) value += 650;
            if (row.contains(rowPattern[5]) || row.contains(rowPattern[5].reverse()) ||
                    row.contains(rowPattern[6]) || row.contains(rowPattern[6].reverse())) value += 330;
            if (row.contains(rowPattern[6]) || row.contains(rowPattern[6].reverse())) value += 330;
            if (row.contains(rowPattern[5]) || row.contains(rowPattern[5].reverse())) value += 250;
            if (row.contains(rowPattern[7]) || row.contains(rowPattern[7].reverse()) ||
                    row.contains(rowPattern[8]) || row.contains(rowPattern[8].reverse())) value += 180;
            if (row.contains(rowPattern[10]) || row.contains(rowPattern[10].reverse())) value += 100;
            if (row.contains(rowPattern[9]) || row.contains(rowPattern[9].reverse())) value += 70;
            if (row.contains(rowPattern[11]) || row.contains(rowPattern[11].reverse())) value += 780;
        }
        return value;
    }

    private void evaluateForLastTurn(Board board, List<BoardPoint> playedCells) {
        ArrayList<BoardPoint> evaluatedTurns = new ArrayList<>();
        for (BoardPoint playedPoint : playedCells) {
            for (int i = playedPoint.getVertical() - 2; i <= playedPoint.getVertical() + 2; i++) {
                for (int j = playedPoint.getHorizontal() - 2; j <= playedPoint.getHorizontal() + 2; j++) {
                    if (i >= 0 && i < 15 && j >= 0 && j < 15 && board.get(i, j) == null &&
                            !evaluatedTurns.contains(board.findPoint(i, j))) {
                        BoardPoint currentPoint = board.findPoint(i, j);
                        int offensiveTurnValue = evaluateTurn(board, currentPoint, board.getTurn().getSide());
                        int defensiveTurnValue = evaluateTurn(board, currentPoint, board.getTurn().getSide().opposite());
                        potentialTurns.put(offensiveTurnValue + defensiveTurnValue, currentPoint);
                        evaluatedTurns.add(currentPoint);
                    }
                }
            }
        }
    }
}


