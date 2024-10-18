package ithaic.imitate_os.fileManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
    private String oldStr;
    private static final String CONFIRM = "确定";
    private static final String CANCEL = "取消";

    public char[] popUp() {
        Stage stage = createStage();
        VBox vBox = createVBox();
        Scene scene = new Scene(vBox, 600, 340);

        stage.setScene(scene);
        stage.showAndWait();

        return StrToCharArray(str);

    }
    private Stage createStage() {
        Stage stage = new Stage();
        String content = "请输入文本：";
        stage.setTitle(content);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.getIcons().add(OS_IMAGE);
        // 设置文本区域的最大高度
        int MAX_INPUT_HEIGHT = 100;
        textArea.setMaxHeight(MAX_INPUT_HEIGHT);
        textArea.setMinHeight(300);
        textArea.setEditable(true);
        textArea.setWrapText(true);

        oldStr = textArea.getText();


        addTextAreaListeners();
        return stage;
    }
    private VBox createVBox() {
        // 创建按钮
        HBox hBox = createButtonHBox();
        VBox vBox = new VBox(textArea, hBox);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }
    private HBox createButtonHBox() {
        Label confirm = new Label(CONFIRM);
        Label close = new Label(CANCEL);

        HBox hBoxConfirm = new HBox(confirm);
        HBox hBoxClose = new HBox(close);

        // 点击事件
        hBoxConfirm.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                this.str = textArea.getText();
                closeStage();
            }
        });

        hBoxClose.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                this.str = oldStr;
                closeStage();
            }
        });

        // 设置按钮高度
        hBoxConfirm.setPrefHeight(40);
        hBoxClose.setPrefHeight(40);

        // 设置按钮居中
        HBox hBox = new HBox(hBoxConfirm, hBoxClose);
        hBox.setStyle("-fx-font-size:15;");
        HBox.setHgrow(hBoxConfirm, Priority.ALWAYS);
        HBox.setHgrow(hBoxClose, Priority.ALWAYS);

        // 悬停效果
        setButtonHoverEffect(hBoxConfirm, confirm, "#5677fc", "#0029cc", "white");
        setButtonHoverEffect(hBoxClose, close, "rgb(239,239,239)", "#dcdcdc", null);

        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }
    private void setButtonHoverEffect(HBox buttonBox, Label buttonLabel, String normalColor, String hoverColor, String textColor) {
        buttonBox.setStyle("-fx-alignment:CENTER;-fx-background-color: " + normalColor + ";");

        buttonBox.setOnMouseEntered(event -> {
            buttonBox.setStyle("-fx-alignment:CENTER;-fx-background-color: " + hoverColor + ";"); // 悬停时的背景颜色
            if (textColor != null) {
                buttonLabel.setStyle("-fx-text-fill:" + textColor + ";");
            }
        });

        buttonBox.setOnMouseExited(event -> {
            buttonBox.setStyle("-fx-alignment:CENTER;-fx-background-color: " + normalColor + ";"); // 离开时的背景颜色
            if (textColor != null) {
                buttonLabel.setStyle("-fx-text-fill:black;");
            }
        });
    }

    private void closeStage() {
        Stage stage = (Stage) textArea.getScene().getWindow();
        stage.close();
    }


    private void addTextAreaListeners() {
        textArea.setOnKeyPressed(event -> {
            // 检查是否按下 Ctrl + Enter
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
               //执行确定
                confirmInput();
                event.consume(); // 防止事件进一步传播
            }
            // 检查是否按下 Ctrl + DEL
            if (event.isControlDown() && event.getCode() == KeyCode.DELETE) {
                //执行确定
                cancelInput();
                event.consume();
            }
        });
    }
    private void confirmInput() {
        this.str = textArea.getText();
        closeStage(); // 关闭窗口
    }
    private void cancelInput(){
        this.str = oldStr;
     //   textArea.setText(str);
        closeStage();
    }
    public void appendText(String str) {
        textArea.appendText(str);
    }

    private char[] StrToCharArray(String str) {
        if (str==null) return null;
        return str.toCharArray();
    }


}
