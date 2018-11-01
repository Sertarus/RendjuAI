package javafx;

import core.Board;
import core.BoardPoint;
import core.Stone;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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
import javafx.stage.StageStyle;


import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Board board = new Board(15, 15);

    private Label status = new Label("");

    private Map<BoardPoint, Group> buttons = new HashMap<>();

    private boolean inProcess = false;

    private double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    private Button pass = new Button("Pass turn");

    private AtomicInteger passesInARow = new AtomicInteger();

    private ArrayList<BoardPoint> playedCells = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        final Stage choosePlayerDialog = new Stage(StageStyle.UNDECORATED);
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
                    if (inProcess && board.getTurn().getComputerLogic() == null) {
                        BoardPoint turnPoint = board.makeTurn(finalColumn, finalRow);
                        if (turnPoint != null) {
                            updateBoard(boardPoint);
                            passesInARow.set(0);
                            updateStatus();
                            computerTurn();
                            updateStatus();

                        }
                    }
                });
                group.setOnMouseEntered(event -> {
                    if (board.get(finalColumn, finalRow) == null && inProcess &&
                            board.getTurn().getComputerLogic() == null) {
                        Circle possibleStone = new Circle(screenHeight * 2 / 75, screenHeight * 2 / 75,
                                screenHeight * 13 / 600);
                        possibleStone.setOpacity(0.5);
                        if (board.getTurn().getSide() == Stone.WHITE) {
                            possibleStone.setFill(Color.WHITE);
                        }
                        group.getChildren().add(possibleStone);
                    }
                });
                group.setOnMouseExited(event -> {
                    if (board.get(finalColumn, finalRow) == null && inProcess &&
                            board.getTurn().getComputerLogic() == null) {
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
            inProcess = false;
            board.clear();
            if (board.getFirstPlayer().getComputerLogic() != null) {
                board.getFirstPlayer().getComputerLogic().clearPotentialTurns();
            }
            if (board.getSecondPlayer().getComputerLogic() != null) {
                board.getSecondPlayer().getComputerLogic().clearPotentialTurns();
            }
            for (BoardPoint boardPoint : buttons.keySet()) {
                updateBoard(boardPoint);
            }
            updateStatus();
            passesInARow.set(0);
            playedCells.clear();
            if (!pass.getStyleClass().contains("pass1")) pass.getStyleClass().add("pass1");
            choosePlayerDialog.show();
            computerTurn();
        });
        restart.setLayoutX(screenHeight / 90);
        restart.setLayoutY(screenHeight * 2 / 3);
        pass.setShape(new Ellipse(screenHeight * 13 / 90, screenHeight / 10));
        pass.setPrefSize(screenHeight * 13 / 90, screenHeight / 10);
        pass.setFont(Font.font("Monotype Corsiva", screenHeight / 45));
        pass.getStyleClass().add("pass1");
        pass.setOnAction(event -> {
            if (inProcess && board.getTurn().getComputerLogic() == null) {
                if (board.getStonesOnBoard() >= 6) {
                    if (board.getTurn().getSide() == Stone.BLACK) board.setTurn(board.getSecondPlayer());
                    else board.setTurn(board.getFirstPlayer());
                    passesInARow.getAndIncrement();
                    updateStatus();
                    computerTurn();
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
        choosePlayerDialog.initModality(Modality.APPLICATION_MODAL);
        choosePlayerDialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        Text firstPlayerText = new Text("Choose first player");
        firstPlayerText.setFont(Font.font("Monotype Corsiva", 20));
        Text secondPlayerText = new Text("Choose second player");
        secondPlayerText.setFont(Font.font("Monotype Corsiva", 20));
        dialogVbox.getChildren().add(firstPlayerText);
        final ToggleGroup firstGroup = new ToggleGroup();
        RadioButton rb1 = new RadioButton();
        rb1.getStyleClass().add("radio-button");
        rb1.setText("Human");
        rb1.setFont(Font.font("Monotype Corsiva", 15));
        rb1.setToggleGroup(firstGroup);
        rb1.setSelected(true);
        dialogVbox.getChildren().add(rb1);
        RadioButton rb2 = new RadioButton();
        rb2.setText("Computer");
        rb2.setFont(Font.font("Monotype Corsiva", 15));
        rb2.setToggleGroup(firstGroup);
        dialogVbox.getChildren().add(rb2);
        dialogVbox.getChildren().add(secondPlayerText);
        final ToggleGroup secondGroup = new ToggleGroup();
        RadioButton rb3 = new RadioButton();
        rb3.setText("Human");
        rb3.setFont(Font.font("Monotype Corsiva", 15));
        rb3.setToggleGroup(secondGroup);
        rb3.setSelected(true);
        dialogVbox.getChildren().add(rb3);
        RadioButton rb4 = new RadioButton();
        rb4.setText("Computer");
        rb4.setFont(Font.font("Monotype Corsiva", 15));
        rb4.setToggleGroup(secondGroup);
        dialogVbox.getChildren().add(rb4);
        Button ok = new Button("OK");
        ok.setPrefSize(100, 50);
        HBox hBox = new HBox();
        hBox.getChildren().add(ok);
        hBox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(hBox);
        dialogVbox.getStyleClass().add("vBox");
        dialogVbox.setPadding(new Insets(10, 10, 10, 10));
        Scene dialogScene = new Scene(dialogVbox, 300, 370);
        dialogScene.getStylesheets().add("ObjectStyles.css");
        ok.getStyleClass().add("restart");
        ok.setShape(new Ellipse(100, 120));
        ok.setOnAction(event -> {
            if (rb1.isSelected()) {
                board.getFirstPlayer().setComputerLogic(null);
            }
            if (rb2.isSelected()) {
                board.getFirstPlayer().setComputerLogic(board.getFirstComputer());
            }
            if (rb3.isSelected()) {
                board.getSecondPlayer().setComputerLogic(null);
            }
            if (rb4.isSelected()) {
                board.getSecondPlayer().setComputerLogic(board.getSecondComputer());
            }
            inProcess = true;
            if (board.getFirstPlayer().getComputerLogic() != null &&
                    board.getSecondPlayer().getComputerLogic() != null) {
                Timer timer = new Timer(true);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            computerTurn();
                            if (!inProcess) timer.cancel();
                        });
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 1000, 2000);
            }
            choosePlayerDialog.hide();
            computerTurn();
        });
        choosePlayerDialog.setScene(dialogScene);
        choosePlayerDialog.show();
        updateStatus();

        computerTurn();
    }

    private void updateStatus() {
        Stone winner = board.winner();
        if (pass.getStyleClass().contains("pass1") && board.getStonesOnBoard() > 5) {
            pass.getStyleClass().remove(pass.getStyleClass().size() - 1);
            pass.getStyleClass().add("pass2");
        }
        String statusString;
        if (!board.hasPossibilityOfVictoryRow() || passesInARow.get() == 2) {
            inProcess = false;
            statusString = "Game status:\nDraw. Press restart to play again.";
        } else if (winner == Stone.BLACK) {
            inProcess = false;
            statusString = "Game status:\nBlack wins. Press restart to play again.";
        } else if (winner == Stone.WHITE) {
            inProcess = false;
            statusString = "Game status:\nWhite wins. Press restart to play again.";
        } else if (board.getTurn().getSide() == Stone.BLACK) {
            statusString = "Game status:\nBlacks turn.";
        } else statusString = "Game status:\nWhites turn.";
        status.setText(statusString);
    }

    private void updateBoard(BoardPoint boardPoint) {
        if (boardPoint == null) return;
        Stone stone = board.get(boardPoint);
        playedCells.add(boardPoint);
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

    private void computerTurn() {
        if (inProcess && board.getTurn().getComputerLogic() != null) {
            updateBoard(board.getTurn().getComputerLogic().makeComputerTurn(board, playedCells));
            passesInARow.set(0);
            updateStatus();
        }
    }
}
