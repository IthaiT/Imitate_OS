package ithaic.imitate_os.fileManager;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;


//弹窗类
public class PopUpWindow {
    private static final Image OS_IMAGE = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/OS_icon.png")));

    private final TextArea textArea = new TextArea();
    private String str;
    private static final String CONFIRM = "确定";
    private static final String CANCEL = "取消";

    public char[] popUp() {

        Stage stage = new Stage();
        String content = "请输入指令：";
        stage.setTitle(content);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(OS_IMAGE);
        //设置窗口不可调整大小
        stage.setResizable(false);

      //  Label label = new Label(Content);
        //设置最大高度
        int MAX_INPUT_HEIGHT = 100;
        textArea.setMaxHeight(MAX_INPUT_HEIGHT);
        //设置默认文本区域尺寸
        textArea.setMinHeight(300);

        //设置文本区域可编辑
        textArea.setEditable(true);
        //自动换行,不需要就设置false
        textArea.setWrapText(true);

        Label confirm = new Label(CONFIRM);
        Label close = new Label(CANCEL);

        HBox hBoxConfirm = new HBox(confirm);
        HBox hBoxClose = new HBox(close);
        //点击事件
        hBoxConfirm.setOnMousePressed(e->{
            if (e.getButton()== MouseButton.PRIMARY){
                this.str = textArea.getText();
                stage.close();
            }
        });
        hBoxClose.setOnMousePressed(e->{
            if (e.getButton()==MouseButton.PRIMARY){
                this.str = null;
                stage.close();
            }
        });

        //按钮高度
        hBoxConfirm.setPrefHeight(40);
        hBoxClose.setPrefHeight(40);

        //设置按钮居中
        HBox hBox = new HBox( hBoxConfirm, hBoxClose);
        hBox.setStyle("-fx-font-size:15;");
        HBox.setHgrow(hBoxConfirm, Priority.ALWAYS);
        HBox.setHgrow(hBoxClose, Priority.ALWAYS);
        //样式,居中和背景颜色
        hBoxConfirm.setStyle("-fx-alignment:CENTER;-fx-background-color: #5677fc;");
        hBoxClose.setStyle("-fx-alignment:CENTER;-fx-background-color: rgb(239,239,239);");

        hBox.setAlignment(javafx.geometry.Pos.CENTER);
        //装载
        VBox vBox = new VBox(textArea, hBox);
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(vBox, 600, 340);
        stage.setScene(scene);
        stage.showAndWait();

        return StrToCharArray(textArea.getText());
    }

    public void appendText(String str) {
        textArea.appendText(str);
    }

    private char[] StrToCharArray(String str) {
        return str.toCharArray();
    }


}
