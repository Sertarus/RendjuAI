package javafx;

import core.Board;
import core.BoardPoint;
import core.Stone;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Board board = new Board(15, 15);

    private Label status = new Label("");

    private Map<BoardPoint, Group> buttons = new HashMap<>();

    private boolean inProcess = true;

    private double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    private Button pass = new Button("Pass turn");

    private AtomicInteger passesInARow = new AtomicInteger();

    @Override
    public void start(Stage primaryStage) {
        int columnsAndRowsNumber = 15;
        primaryStage.setTitle("Renju");
        BorderPane borderPane = new BorderPane();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        for (int column = 0; column < columnsAndRowsNumber; column++) {
            for (int row = 0; row < columnsAndRowsNumber; row++) {
                BoardPoint boardPoint = new BoardPoint(column, row);
                ImageView imageView;
                if (row == 0 && column == 0) {
                    imageView = new ImageView(new Image("/images/LT_Corner.png"));
                } else if (row == 0 && column == 14) {
                    imageView = new ImageView(new Image("/images/RT_Corner.png"));
                } else if (row == 14 && column == 0) {
                    imageView = new ImageView(new Image("/images/LB_Corner.png"));
                } else if (row == 0) {
                    imageView = new ImageView(new Image("/images/T_Side.png"));
                } else if (column == 0) {
                    imageView = new ImageView(new Image("/images/L_Side.png"));
                } else if (row == 14 && column == 14) {
                    imageView = new ImageView(new Image("/images/RB_Corner.png"));
                } else if (row == 14) {
                    imageView = new ImageView(new Image("/images/B_Side.png"));
                } else if (column == 14) {
                    imageView = new ImageView(new Image("/images/R_Side.png"));
                } else imageView = new ImageView(new Image("/images/Center.png"));
                imageView.setFitHeight(screenHeight * 0.8 / 15);
                imageView.setFitWidth(screenHeight * 0.8 / 15);
                Group group = new Group(imageView);
                int finalRow = row;
                int finalColumn = column;
                group.setOnMouseClicked(event -> {
                    if (inProcess) {
                        BoardPoint turnPoint = board.makeTurn(finalColumn, finalRow);
                        if (turnPoint != null) {
                            updateBoard(boardPoint);
                            updateStatus();
                            passesInARow.set(0);
                        }
                    }
                });
                group.setOnMouseEntered(event -> {
                    if (board.get(finalColumn, finalRow) == null && inProcess) {
                        Circle possibleStone = new Circle(screenHeight * 2 / 75, screenHeight * 2 / 75,
                                screenHeight * 13 / 600);
                        possibleStone.setOpacity(0.5);
                        if (board.getTurn() == Stone.WHITE) {
                            possibleStone.setFill(Color.WHITE);
                        }
                        group.getChildren().add(possibleStone);
                    }
                });
                group.setOnMouseExited(event -> {
                    if (board.get(finalColumn, finalRow) == null && inProcess) {
                        group.getChildren().remove(1);
                    }
                });
                buttons.put(boardPoint, group);
                grid.add(group, column, row);
            }
        }
        Button restart = new Button("Restart game");
        restart.setShape(new Ellipse(screenHeight * 13 / 90, screenHeight / 10));
        restart.setPrefSize(screenHeight * 13 / 90, screenHeight / 10);
        restart.setFont(Font.font("Monotype Corsiva", screenHeight / 45));
        restart.getStyleClass().add("restart");
        restart.setOnAction(event -> {
            board.clear();
            for (BoardPoint boardPoint : buttons.keySet()) {
                updateBoard(boardPoint);
            }
            updateStatus();
            inProcess = true;
            passesInARow.set(0);
        });
        restart.setLayoutX(screenHeight / 90);
        restart.setLayoutY(screenHeight * 2 / 3);
        pass.setShape(new Ellipse(screenHeight * 13 / 90, screenHeight / 10));
        pass.setPrefSize(screenHeight * 13 / 90, screenHeight / 10);
        pass.setFont(Font.font("Monotype Corsiva", screenHeight / 45));
        pass.getStyleClass().add("pass1");
        pass.setOnAction(event -> {
            if (inProcess) {
                if (board.getStonesOnBoard() >= 6) {
                    board.setTurn(board.getTurn().opposite());
                    passesInARow.getAndIncrement();
                    updateStatus();
                }
            }
        });
        pass.setLayoutX(screenHeight / 90);
        pass.setLayoutY(screenHeight * 3 / 9);
        status.setWrapText(true);
        status.setAlignment(Pos.TOP_LEFT);
        status.setPrefSize(screenHeight * 3 / 16, screenHeight / 9);
        status.setFont(Font.font("Monotype Corsiva", screenHeight / 45));
        Pane pane = new Pane();
        pane.getChildren().addAll(status, restart, pass);
        VBox vBox = new VBox();
        vBox.setPrefSize(screenHeight * 3 / 16, screenHeight * 0.8);
        vBox.getStyleClass().add("vBox");
        vBox.getChildren().add(pane);
        borderPane.setLeft(vBox);
        borderPane.setCenter(grid);
        Scene scene = new Scene(borderPane, grid.getPrefWidth() + vBox.getWidth(), screenHeight * 0.8);
        scene.getStylesheets().add("ObjectStyles.css");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        updateStatus();
    }

    private void updateStatus() {
        Stone winner = board.winner();
        if (pass.getStyleClass().contains("pass1") && board.getStonesOnBoard() > 5) {
            pass.getStyleClass().remove(pass.getStyleClass().size() - 1);
            pass.getStyleClass().add("pass2");
        }
        String statusString = "";
        if (!board.hasPossibilityOfVictoryRow() || passesInARow.get() == 2) {
            inProcess = false;
            statusString = "Game status:\nDraw. Press restart to play again.";
        } else if (winner == Stone.BLACK) {
            inProcess = false;
            statusString = "Game status:\nBlack wins. Press restart to play again.";
        } else if (winner == Stone.WHITE) {
            inProcess = false;
            statusString = "Game status:\nWhite wins. Press restart to play again.";
        } else if (board.getTurn() == Stone.BLACK) {
            statusString = "Game status:\nBlacks turn.";
        } else statusString = "Game status:\nWhites turn.";
        status.setText(statusString);
    }

    private void updateBoard(BoardPoint boardPoint) {
        if (boardPoint == null) return;
        Stone stone = board.get(boardPoint);
        if (stone == Stone.BLACK) {
            Circle blackStone = new Circle(screenHeight * 2 / 75, screenHeight * 2 / 75, screenHeight * 13 / 600);
            if (buttons.get(boardPoint).getChildren().size() > 1) {
                buttons.get(boardPoint).getChildren().remove(1);
            }
            buttons.get(boardPoint).getChildren().add(blackStone);
        } else if (stone == Stone.WHITE) {
            Circle whiteStone = new Circle(screenHeight * 2 / 75, screenHeight * 2 / 75, screenHeight * 13 / 600);
            whiteStone.setStroke(Color.BLACK);
            if (buttons.get(boardPoint).getChildren().size() > 1) {
                buttons.get(boardPoint).getChildren().remove(1);
            }
            whiteStone.setFill(Color.WHITE);
            buttons.get(boardPoint).getChildren().add(whiteStone);
        } else {
            if (buttons.get(boardPoint).getChildren().size() > 1) {
                buttons.get(boardPoint).getChildren().remove(1);
            }
        }
    }
}
