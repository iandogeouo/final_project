package final_project;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class ConGame {

    @FXML private Label comboLabel;
    @FXML private GridPane grid;
    private Game game;
    private Button[][] buttonGrid;
    @FXML private Label score;
    @FXML private Label step;

    @FXML
    public void initialize() {
        game = new Game(8, 10);
        buttonGrid = new Button[8][10];
        renderBoard();
        score.setText(game.getScore());
        step.setText(game.getStep());
    }

    private void renderBoard() {
        step.setText( game.getStep());
        score.setText( game.getScore());
        grid.getChildren().clear();
        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                int candyType = game.getCandy(i, j);
                Button btn = new Button();
                ImageView img = new ImageView(game.getemoji(candyType));
                img.setFitWidth(40);
                img.setFitHeight(40);
                img.setPreserveRatio(true);
                btn.getStyleClass().add("emoji_button");
                btn.setGraphic(img);
                btn.setMinSize(50, 50);
                btn.setUserData(new int[]{i, j});
                btn.setOnAction(e -> handleClick(btn));
                grid.add(btn, j, i);
                buttonGrid[i][j] = btn;
            }
        }
    }

    private Button firstSelected = null;

    private void handleClick(Button btn) {
        if (firstSelected == null) {
            firstSelected = btn;
            btn.setStyle("-fx-border-color: #2196F3; -fx-border-width: 3px;");
            return;
        }

        int[] a = (int[]) firstSelected.getUserData();
        int[] b = (int[]) btn.getUserData();

        Button btn1 = firstSelected;
        Button btn2 = btn;

        firstSelected.setStyle("-fx-background-color: transparent;");
        firstSelected = null;

        if (a[0] == b[0] && a[1] == b[1]) return;

        boolean adjacent = Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) == 1;

        if (adjacent) {
            animateSwap(btn1, btn2, () -> {
                game.swap(a[0], a[1], b[0], b[1]);
                renderBoard();
                step.setText( game.getStep());


                PauseTransition pause = new PauseTransition(Duration.seconds(0.01));
                pause.setOnFinished(e -> {
                    if (!game.hasMatch()) {
                        Button btnBack1 = buttonGrid[b[0]][b[1]];
                        Button btnBack2 = buttonGrid[a[0]][a[1]];
                        game.swap(b[0], b[1], a[0], a[1]);
                        animateSwap(btnBack1, btnBack2, this::renderBoard);
                    } else {
                        game.move();
                        processBoardAnimated();
                    }

                });
                pause.play();
            });
        }


    }

    private void animateSwap(Button btn1, Button btn2, Runnable onFinished) {
        if (btn1 == null || btn2 == null) return;

        int[] a = (int[]) btn1.getUserData();
        int[] b = (int[]) btn2.getUserData();

        double dx = (b[1] - a[1]) * 50;
        double dy = (b[0] - a[0]) * 50;

        TranslateTransition t1 = new TranslateTransition(Duration.millis(200), btn1);
        t1.setByX(dx);
        t1.setByY(dy);

        TranslateTransition t2 = new TranslateTransition(Duration.millis(200), btn2);
        t2.setByX(-dx);
        t2.setByY(-dy);

        ParallelTransition pt = new ParallelTransition(t1, t2);
        pt.setOnFinished(e -> {
            btn1.setTranslateX(0);
            btn1.setTranslateY(0);
            btn2.setTranslateX(0);
            btn2.setTranslateY(0);
            onFinished.run();
        });
        pt.play();
    }

    private void animateClear(List<int[]> matched, Runnable onFinished) {
        ParallelTransition pt = new ParallelTransition();

        for (int[] pos : matched) {
            Button btn = buttonGrid[pos[0]][pos[1]];
            if (btn != null) {
                FadeTransition fade = new FadeTransition(Duration.millis(200), btn.getGraphic());
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                pt.getChildren().add(fade);
            }
        }

        pt.setOnFinished(e -> onFinished.run());
        pt.play();
    }

    private void showCombo(int combo) {
        if (combo <= 1) return;

        comboLabel.setText("Combo x" + combo + "!!");
        comboLabel.setOpacity(0);
        comboLabel.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), comboLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.millis(1000));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), comboLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.setOnFinished(e -> comboLabel.setVisible(false));
        seq.play();
    }


    private void processBoardAnimated() {
        int[] combo = {1};

        Runnable loop = new Runnable() {
            @Override
            public void run() {
                List<int[]> matched = game.getMatchedCoords();
                if (!matched.isEmpty()) {
                    animateClear(matched, () -> {
                        game.clearMarked(matched, combo[0]);
                        showCombo(combo[0]);
                        score.setText(game.getScore());
                        game.dropCandies();
                        renderBoard();

                        PauseTransition pause = new PauseTransition(Duration.millis(100));
                        pause.setOnFinished(e -> {
                            game.fillNewCandies();
                            renderBoard();
                            combo[0]++;
                            Platform.runLater(this); // 繼續 combo 處理
                        });
                        pause.play();
                    });
                } else {
                    // ✅ 在動畫全部跑完、沒有再連鎖時才檢查是否遊戲結束
                    checkGameEnd();
                }
            }
        };
        Platform.runLater(loop);
    }



    private void checkGameEnd() {
        if (Integer.parseInt(game.getStep()) <= 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("finish.fxml"));
                Parent root = loader.load();
                ConFinish controller = loader.getController(); // 拿到 ConFinal 控制器
                controller.setScore(Integer.parseInt(game.getScore())); // 傳遞分數
                Stage stage = (Stage) grid.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
