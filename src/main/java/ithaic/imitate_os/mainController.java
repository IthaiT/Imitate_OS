package ithaic.imitate_os;

import ithaic.imitate_os.fileManager.FileInteract;
import ithaic.imitate_os.fileManager.PopUpWindow;
import ithaic.imitate_os.process.CPU;
import ithaic.imitate_os.process.PCB;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;

import java.util.Queue;
import java.util.stream.Collectors;

public class mainController {
    @FXML
    private VBox mainVBox;
    @FXML
    private HBox topHBox;
    @FXML
    private VBox bottom_leftBox;
    @FXML
    private SplitPane bottom_rightBox;
    @FXML
    private SplitPane bottom_Box;
    @FXML
    private HBox queueBox;
    @FXML
    private VBox commandBox;
    @FXML

    private SplitPane diskBox;
    @FXML
    private VBox processBox;
    @FXML
    private VBox userInterface;
    @FXML
    private HBox processAndDisk;
    @FXML
    private HBox userInterface_box;
    @FXML
    private TextArea historyCommand;
    @FXML
    private Label systemClockLabel;
    @FXML
    private Label relativeClockLabel;
    @FXML
    private Label runningProcessLabel;
    @FXML
    private ListView readyProcessQueue;
    @FXML
    private ListView blockProcessQueue;
    @FXML
    private TreeView diskStructure;

    @FXML
    private TextField CommandInput;
    @FXML
    private Button button;
    private ObservableList<String> currentProcessIDs_ready = FXCollections.observableArrayList();
    @FXML
    private void initialize() {
        new FileInteract(CommandInput,historyCommand,button);
        initializeBox();
        initializeText();
        timeUpdate();
        initializeQueueBox();
    }

//    初始化大框组件的边界
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


//    初始化输入命令行框和历史命令行的边界
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

    //    初始化进程队列框
    private void initializeQueueBox(){
        readyProcessQueue.prefWidthProperty().bind(Bindings.multiply(0.5,queueBox.prefWidthProperty()));
        blockProcessQueue.prefWidthProperty().bind(Bindings.multiply(0.5,queueBox.prefWidthProperty()));
    }
    //    更新进程框
    private void processQueueUpdate(){
        readyProcessUpdate();
    }

    //    就绪进程的更新实现
    private void readyProcessUpdate(){
       Queue<PCB> pcbQueue= CPU.getInstance().getProcessManager().getReadyProcessQueue();
       ObservableList<String> newProcessIDs=FXCollections.observableArrayList();
       if (pcbQueue.size()==0){
           newProcessIDs.add("无进程");
       }else {
           for (PCB pcb:pcbQueue){
               newProcessIDs.add(String.valueOf(pcb.getPid()));
           }
       }
        // 只在列表发生变化时更新 ListView
        if (!newProcessIDs.equals(currentProcessIDs_ready)) {
            currentProcessIDs_ready.setAll(newProcessIDs);  // 更新 currentProcessIDs
            readyProcessQueue.setItems(currentProcessIDs_ready);  // 只在需要时设置
        }
    }
//    组件依靠时间间隔更新一次，时钟进程
    private void timeUpdate(){
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
           updateClock();
           updateProcess();
           processQueueUpdate();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

//    系统时间个时间片获取的实现
    private void updateClock(){
        int systemClock= CPU.getInstance().getLabelClock();
        int relativeClock= CPU.getInstance().getLabelRelativeClock();
        systemClockLabel.setText("系统时间："+systemClock);
        relativeClockLabel.setText("时间片："+relativeClock);
    }

//    运行中进程更新
    private void updateProcess(){
        //当前进程非空
        if (CPU.getInstance().getRunningProcess()!=null){
            int runningProcessID=CPU.getInstance().getRunningProcess().getPid();
            runningProcessLabel.setText("运行进程："+runningProcessID);
        }else {//当前无进程
            runningProcessLabel.setText("运行进程：无");
        }
    }
}
