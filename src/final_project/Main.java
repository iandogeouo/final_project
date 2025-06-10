package final_project; // 替換為實際使用的包名

import java.lang.*;
import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


public class Main extends Application {




    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml")); // 載入 FXML 文件
        stage.setTitle("Emoji Crush");
        stage.setScene(new Scene(root, 1000, 600)); // 設定視窗大小
        stage.show(); // 顯示視窗

    }

    public static void main(String[] args) {
        launch(args); // 啟動應用程式
    }
}
