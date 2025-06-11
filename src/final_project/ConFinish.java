package final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ConFinish {

    @FXML private Button btn;
    @FXML private Label score;

    @FXML
    public void initialize(){
        btn.getStyleClass().add("finish_button");
    }

    public void setScore(int final_score) {
        score.setText("Your Score：" + final_score);
    }

    @FXML
    public void return_start(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("css/button.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
