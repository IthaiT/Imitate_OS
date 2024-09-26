package ithaic.imitate_os;

import ithaic.imitate_os.fileManager.FileInteract;
import ithaic.imitate_os.fileManager.PopUpWindow;
import ithaic.imitate_os.process.CPU;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;

public class mainController {
    public VBox mainVBox;
    public HBox topHBox;
    public VBox bottom_leftBox;
    
    public SplitPane bottom_rightBox;
    public SplitPane bottom_Box;
    public HBox queueBox;
    public VBox commandBox;

    public VBox diskBox;
    public VBox processBox;
    public VBox userInterface;
    public HBox processAndDisk;
    public HBox userInterface_box;
    public TextArea historyCommand;
    public Label systemClockLabel;
    @FXML
    private TextField CommandInput;
    @FXML
    private Button button;

    @FXML
    private void initialize() {
        new FileInteract(CommandInput,historyCommand,button);
        initializeBox();
        initializeText();
        initializeTopBox();

    }
    private void initializeBox() {
        topHBox.setPrefHeight(mainVBox.getPrefHeight() * 0.05);

        bottom_Box.setPrefHeight(mainVBox.getPrefHeight() * 0.95);
        bottom_Box.prefHeightProperty().bind(Bindings.subtract(mainVBox.heightProperty(),topHBox.heightProperty()));

        bottom_leftBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_leftBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.4);
        bottom_leftBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_leftBox.prefWidthProperty().bind(Bindings.multiply(0.4,bottom_Box.prefWidthProperty()));


            queueBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.4);
//            绑定queueBox的高度是父组件高度的0.4倍
            queueBox.prefHeightProperty().bind(Bindings.multiply(0.4,bottom_leftBox.heightProperty()));

            commandBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.6);
//            绑定commandBox的高度是父组件高度的0.6倍
            commandBox.prefHeightProperty().bind(Bindings.multiply(0.6,bottom_leftBox.heightProperty()));

        bottom_rightBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_rightBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.6);
        bottom_rightBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_rightBox.prefWidthProperty().bind(Bindings.multiply(0.6,bottom_Box.prefWidthProperty()));


        processAndDisk.setPrefHeight(bottom_rightBox.getPrefHeight() * 0.7);
        processAndDisk.prefHeightProperty().bind(Bindings.multiply(0.7,bottom_rightBox.prefHeightProperty()));
              processBox.setPrefWidth(bottom_rightBox.getPrefWidth()*0.4);
              processBox.prefWidthProperty().bind(Bindings.multiply(0.4,processAndDisk.prefWidthProperty()));

              diskBox.setPrefWidth(processAndDisk.getPrefWidth() *0.6);
              diskBox.prefWidthProperty().bind(Bindings.multiply(0.6,processAndDisk.prefWidthProperty()));

        userInterface.setPrefHeight(bottom_rightBox.getPrefHeight() *0.3);
        userInterface.prefHeightProperty().bind(Bindings.multiply(0.3,bottom_rightBox.prefHeightProperty()));
            historyCommand.setMinHeight(userInterface.getPrefHeight()*0.3);
    }

    private void initializeText(){
        //不可编辑
        historyCommand.setEditable(false);
        //设置historyCommand的高度为userInterface-底下输入栏的高度
        historyCommand.prefHeightProperty().bind(Bindings.subtract(userInterface.heightProperty(),userInterface_box.heightProperty()));
        button.setPrefWidth(100);
        //设置CommandInput的宽度为userInterface的宽度减去button的宽度,二者撑满底部
        CommandInput.setPrefWidth(userInterface.getPrefWidth()-button.getPrefWidth());
        CommandInput.prefWidthProperty().bind(Bindings.subtract(userInterface.widthProperty(),button.widthProperty()));
    }

    private void initializeTopBox(){
        updateClock();
    }
    public void updateClock(){

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
               int systemClock= CPU.getInstance().getLabelClock();
               systemClockLabel.setText("系统时间："+systemClock);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

    }

}
