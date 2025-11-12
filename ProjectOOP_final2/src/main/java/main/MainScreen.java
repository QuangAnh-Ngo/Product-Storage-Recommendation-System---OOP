package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static controller.ControlMainScreen.setStage;

public class MainScreen extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader  loader = new FXMLLoader(getClass().getResource("/com/example/screen/ShowMainScreen.fxml"));
        Parent root = loader.load();
        setStage(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //stage.initStyle(StageStyle.DECORATED);
        stage.setFullScreen(true);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
