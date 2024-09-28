package ithaic.imitate_os;

import ithaic.imitate_os.process.CPU;
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

    //关闭程序(关闭窗口一同应该关闭CPU运行线程)
    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        //CPU初始化
        CPU cpu = CPU.getInstance();
        cpu.run();
        //UI窗口启动
        launch();
    }
}