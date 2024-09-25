package ithaic.imitate_os;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private final static String TITLE = "Imitate OS";
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }
    //OS界面缺失图标

    public static void main(String[] args) {
        launch();
    }
}