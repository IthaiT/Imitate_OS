package ithaic.imitate_os.fileManager;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;



//弹窗类
public class PopUpWindow {
    private final int MAX_INPUT_HEIGHT = 100;
    private TextArea textArea = new TextArea();
    private final String Title = "弹窗";
    private final String Content = "请输入指令：";
    private String str;
    public char[] popUp() {
        String str = "";
        Stage stage = new Stage();
        stage.setTitle(Title);
        stage.initModality(Modality.APPLICATION_MODAL);
        //设置窗口不可调整大小
        stage.setResizable(false);

        Label label = new Label(Content);
        //设置最大高度
        textArea.setMaxHeight(MAX_INPUT_HEIGHT);
        //设置默认文本区域尺寸
        textArea.setMinHeight(300);
        //设置文本区域可编辑
        textArea.setEditable(true);
        //自动换行,不需要就设置false
        textArea.setWrapText(true);

        Button btn1 = new Button("确定");
        btn1.setPrefWidth(50);
        btn1.setOnAction(e -> {
            this.str = textArea.getText();
            stage.close();
        });
        Button btn2 = new Button("取消");
        btn2.setPrefWidth(50);
        btn2.setOnAction(e -> {
            this.str="";
            stage.close();
        });
        //设置按钮居中
        HBox hBox = new HBox(20, btn1, btn2);
        hBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox vBox = new VBox(10, label, textArea, hBox);
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(vBox, 700, 400);
        stage.setScene(scene);
        stage.showAndWait();

        return StrToCharArray(textArea.getText());
    }

    public void appendText(String str) {
//        textArea.setText(str);
        textArea.appendText(str);
    }
    private char[] StrToCharArray(String str) {
        return str.toCharArray();
    }


}
