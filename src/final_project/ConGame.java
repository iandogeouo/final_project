package final_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.lang.*;
import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.*;

public class ConGame {

    @FXML private GridPane grid;

    private Game game; // 管理邏輯的類別

    @FXML
    public void initialize() {
        game = new Game(8, 10);  // 建盤面
        renderBoard();
    }

    private void renderBoard() {
        // 清空、重建 GridPane 的內容
        grid.getChildren().clear();
        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                int candyType = game.getCandy(i, j);
                Button btn = new Button();
                ImageView img = new ImageView(game.getemoji(candyType));
                img.setFitWidth(40);
                img.setFitHeight(40);
                img.setPreserveRatio(true);  // 比例正常
                btn.getStyleClass().add("emoji_button");
                btn.setGraphic(img);
                btn.setMinSize(50, 50);
                btn.setUserData(new int[]{i, j});
                btn.setOnAction(e -> handleClick(btn));
                grid.add(btn, j, i);
            }
        }
    }
    private Button firstSelected = null;

    private void handleClick(Button btn) {
        if (firstSelected == null) {
            // 第一次點擊，記住按鈕
            firstSelected = btn;
            btn.setStyle("-fx-border-color: #2196F3; -fx-border-width: 3px;"); // 高亮邊框
            return;
        }

        // 第二次點擊
        int[] a = (int[]) firstSelected.getUserData();
        int[] b = (int[]) btn.getUserData();

        // 移除第一顆的選取樣式
        firstSelected.setStyle("-fx-background-color: transparent;");

        // 如果是點到同一顆，取消選擇
        if (a[0] == b[0] && a[1] == b[1]) {
            firstSelected = null;
            return;
        }

        // 判斷是否相鄰（上下左右）
        boolean adjacent = Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) == 1;

        if (adjacent) {
            game.swap(a[0], a[1], b[0], b[1]);


             if (!game.hasMatch()) {
                 game.swap(a[0], a[1], b[0], b[1]); // 換回來
             }else{ processBoardAnimated();}
        }

        firstSelected = null; // 重置點擊
    }


    private void processBoardAnimated() {
        if (game.clearMatches()) {
            renderBoard();

            PauseTransition pause1 = new PauseTransition(Duration.seconds(0.3));
            pause1.setOnFinished(e1 -> {
                game.dropCandies();
                renderBoard();

                PauseTransition pause2 = new PauseTransition(Duration.seconds(0.3));
                pause2.setOnFinished(e2 -> {
                    game.fillNewCandies();
                    renderBoard();

                    PauseTransition pause3 = new PauseTransition(Duration.seconds(0.3));
                    pause3.setOnFinished(e3 -> {
                        // 再次檢查連鎖
                        processBoardAnimated();
                    });

                    pause3.play();
                });

                pause2.play();
            });

            pause1.play();
        }
    }



}