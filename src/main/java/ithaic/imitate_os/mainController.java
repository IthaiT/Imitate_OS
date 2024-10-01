package ithaic.imitate_os;

import ithaic.imitate_os.fileManager.DiskUsedShower;
import ithaic.imitate_os.fileManager.FileInteract;

import ithaic.imitate_os.fileManager.DiskTreeShower;
import ithaic.imitate_os.memoryManager.MemoryPaneShower;
import ithaic.imitate_os.process.CPU;
import ithaic.imitate_os.process.PCB;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.Queue;

public class mainController {
    @FXML
    private HBox memoryPane;
    @FXML
    private Label diskBox_VBox_bottom_label;
    @FXML
    private ScrollPane diskScrollPane;
    @FXML
    private VBox diskBox_VBox_top;
    @FXML
    private VBox diskBox_VBox_bottom;
    @FXML
    private FlowPane diskUsedPane;
    @FXML
    private Label currentCommand;
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
    private SplitPane processAndDisk;
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
    private TextArea intermediateProcess;
    @FXML
    private Label processResult;
@FXML
private PieChart pieChart;
    @FXML
    private TextField CommandInput;
    @FXML
    private Button button;

    private ObservableList<String> currentProcessNames_ready = FXCollections.observableArrayList();
    private ObservableList<String> currentProcessNames_block = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        new FileInteract(CommandInput, historyCommand, button);
        new DiskTreeShower(diskStructure);
        new DiskUsedShower(diskUsedPane);
        initializeBox();
        initializeText();
        timeUpdate();
        initializeQueueBox();
        initializeProcessBox();
        Platform.runLater(()->{
            new MemoryPaneShower(memoryPane,bottom_leftBox);
        });


    }

    //    初始化大框组件的边界
    private void initializeBox() {
        topHBox.setPrefHeight(mainVBox.getPrefHeight() * 0.05);

        bottom_Box.setPrefHeight(mainVBox.getPrefHeight() * 0.95);
        bottom_Box.prefHeightProperty().bind(Bindings.subtract(mainVBox.heightProperty(), topHBox.heightProperty()));

        bottom_leftBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_leftBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.4);
        bottom_leftBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_leftBox.prefWidthProperty().bind(Bindings.multiply(0.4, bottom_Box.widthProperty()));


        queueBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.4);
//            绑定queueBox的高度是父组件高度的0.4倍
        queueBox.prefHeightProperty().bind(Bindings.multiply(0.4, bottom_leftBox.heightProperty()));
        commandBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.6);
//            绑定commandBox的高度是父组件高度的0.6倍
        commandBox.prefHeightProperty().bind(Bindings.multiply(0.6, bottom_leftBox.heightProperty()));
                    memoryPane.setPrefWidth(commandBox.getPrefWidth());
                    memoryPane.prefWidthProperty().bind(commandBox.widthProperty());
        bottom_rightBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_rightBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.6);
        bottom_rightBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_rightBox.prefWidthProperty().bind(Bindings.multiply(0.6, bottom_Box.widthProperty()));


        processAndDisk.setPrefHeight(bottom_rightBox.getPrefHeight() * 0.7);
        processAndDisk.prefHeightProperty().bind(Bindings.multiply(0.7, bottom_rightBox.heightProperty()));
        processBox.setPrefWidth(bottom_rightBox.getPrefWidth() * 0.4);
        processBox.prefWidthProperty().bind(Bindings.multiply(0.4, processAndDisk.widthProperty()));

        diskBox.setPrefWidth(processAndDisk.getPrefWidth() * 0.6);
        diskBox.prefWidthProperty().bind(Bindings.multiply(0.6, processAndDisk.widthProperty()));
          diskScrollPane.prefHeightProperty().bind(Bindings.subtract(diskBox_VBox_bottom.heightProperty(),diskBox_VBox_bottom_label.heightProperty()));
        userInterface.setPrefHeight(bottom_rightBox.getPrefHeight() * 0.3);
        userInterface.prefHeightProperty().bind(Bindings.multiply(0.3, bottom_rightBox.heightProperty()));
        historyCommand.setMinHeight(userInterface.getPrefHeight() * 0.3);
    }


    //    初始化输入命令行框和历史命令行的边界
    private void initializeText() {
        //不可编辑
        historyCommand.setEditable(false);
        //设置historyCommand的高度为userInterface-底下输入栏的高度
        historyCommand.prefHeightProperty().bind(Bindings.subtract(userInterface.heightProperty(), userInterface_box.heightProperty()));
        button.setPrefWidth(100);
        //设置CommandInput的宽度为userInterface的宽度减去button的宽度,二者撑满底部
        CommandInput.setPrefWidth(userInterface.getPrefWidth() - button.getPrefWidth());
        CommandInput.prefWidthProperty().bind(Bindings.subtract(userInterface.widthProperty(), button.widthProperty()));
    }

    //    初始化进程队列框
    private void initializeQueueBox() {
        readyProcessQueue.prefWidthProperty().bind(Bindings.multiply(0.5, queueBox.prefWidthProperty()));
        blockProcessQueue.prefWidthProperty().bind(Bindings.multiply(0.5, queueBox.prefWidthProperty()));
    }

    //    初始化进程过程和结果
    private void initializeProcessBox() {
        intermediateProcess.setEditable(false);
        intermediateProcess.prefHeightProperty().bind(Bindings.subtract(processBox.heightProperty(), processResult.heightProperty()));
    }

    //    更新进程框
    private void processQueueUpdate() {
        readyProcessUpdate();
        blockProcessUpdate();
    }

    //    就绪进程的更新实现
    private void readyProcessUpdate() {
        Queue<PCB> pcbQueue = CPU.getInstance().getProcessManager().getReadyProcessQueue();
        ObservableList<String> newProcessNames = FXCollections.observableArrayList();
        if (pcbQueue.size() == 0) {
            newProcessNames.add("无进程");
        } else {
            for (PCB pcb : pcbQueue) {
                newProcessNames.add("["+pcb.getPid()+"] "+pcb.getName());
            }
        }
        // 只在列表发生变化时更新 ListView
        if (!newProcessNames.equals(currentProcessNames_ready)) {
            currentProcessNames_ready.setAll(newProcessNames);  // 更新 currentProcessIDs
            readyProcessQueue.setItems(currentProcessNames_ready);  // 只在需要时设置
        }
    }
    private void blockProcessUpdate(){
        Queue<PCB> pcbQueue = CPU.getInstance().getProcessManager().getBlockedProcessQueue();
        ObservableList<String> newProcessNames = FXCollections.observableArrayList();
        if (pcbQueue.size() == 0) {
            newProcessNames.add("无进程");
        } else {
            for (PCB pcb : pcbQueue) {
                newProcessNames.add("["+pcb.getPid()+"] "+pcb.getName());
            }
        }
        // 只在列表发生变化时更新 ListView
        if (!newProcessNames.equals(currentProcessNames_block)) {
            currentProcessNames_block.setAll(newProcessNames);  // 更新 currentProcessIDs
            blockProcessQueue.setItems(currentProcessNames_block);  // 只在需要时设置
        }
    }

    //    组件依靠时间间隔更新一次，时钟进程
    private void timeUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateClock();
            updateProcess();
            processQueueUpdate();
            updateIntermediateProcess();
            DiskTreeShower.updateDiskTree();
            DiskUsedShower.updateDiskUsed();
            MemoryPaneShower.updateMemoryPane();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //    系统时间个时间片获取的实现
    private void updateClock() {
        int systemClock = CPU.getInstance().getLabelClock();
        int relativeClock = CPU.getInstance().getLabelRelativeClock();
        systemClockLabel.setText("系统时间：" + systemClock);
        relativeClockLabel.setText("时间片：" + relativeClock);
    }

    //    运行中进程更新
    private void updateProcess() {
        //当前进程非空
        if (CPU.getInstance().getRunningProcess() != null) {
            String runningProcessName = CPU.getInstance().getRunningProcess().getName();
            runningProcessLabel.setText("运行进程：" + runningProcessName);
        } else {//当前无进程
            runningProcessLabel.setText("运行进程：无");
        }
    }

    //    中间过程更新
    private void updateIntermediateProcess() {
        if (CPU.getInstance().getRunningProcess() != null) {
            String tmp = CPU.getInstance().getProcessState();
            intermediateProcess.appendText(">" + tmp + "\n");
            //当前指令
            currentCommand.setText("当前执行中指令："+CPU.getInstance().getIR());
            //最终结果输出
            processResult.setText("AX = " + CPU.getInstance().getProcessResult());
        }
    }

}
