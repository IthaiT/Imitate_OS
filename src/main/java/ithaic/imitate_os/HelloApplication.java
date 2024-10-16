package ithaic.imitate_os;
import ithaic.imitate_os.fileManager.DiskTreeShower;
import ithaic.imitate_os.process.CPU;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
public class HelloApplication extends Application {
    private final static String TITLE = "Imitate OS";
    private static final Image OS_IMAGE = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/OS_icon.png")));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("mainView.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.getIcons().add(OS_IMAGE);
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(800);
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