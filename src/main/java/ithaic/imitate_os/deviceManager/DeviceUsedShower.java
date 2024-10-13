package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.fileManager.DiskTreeShower;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.*;

public class DeviceUsedShower {



    /**
     * 是设备的图片/图标
     * */

    private static final Image DEVICE_A = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceA.png")));
    private static final Image DEVICE_B = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceB.png")));
    private static final Image DEVICE_C = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceC.png")));
    /**
     * 直接面对Label设置值,避免重复加载
     * */
    private static final Label A1 = new Label("A1");
    private static final Label A2 = new Label("A2");
    private static final Label B1 = new Label("B1");
    private static final Label B2 = new Label("B2");
    private static final Label B3 = new Label("B3");
    private static final Label C1 = new Label("C1");
    private static final Label C2 = new Label("C2");
    private static final Label C3 = new Label("C3");
    //装载Label的垂直盒子
    private static final VBox vbox_A = new VBox();
    private static final VBox vbox_B = new VBox();
    private static final VBox vbox_C = new VBox();

    //总盒子
    private static HBox deviceBox;
    private static ListView listView;


    //初始化,列表和表格
    public DeviceUsedShower(ListView listView, HBox deviceBeingUsed){
        DeviceUsedShower.listView = listView;
        DeviceUsedShower.deviceBox = deviceBeingUsed;
        initListView();
        initDeviceBox();
    }

    private void initDeviceBox(){
        deviceBox.setStyle(
                "-fx-font-weight:normal;"
               );

        vbox_A.setAlignment(Pos.TOP_CENTER);
        vbox_B.setAlignment(Pos.TOP_CENTER);
        vbox_C.setAlignment(Pos.TOP_CENTER);

        vbox_A.setSpacing(10);
        vbox_B.setSpacing(10);
        vbox_C.setSpacing(10);

        vbox_A.getChildren().add(A1);
        vbox_A.getChildren().add(A2);

        vbox_B.getChildren().add(B1);
        vbox_B.getChildren().add(B2);
        vbox_B.getChildren().add(B3);

        vbox_C.getChildren().add(C1);
        vbox_C.getChildren().add(C2);
        vbox_C.getChildren().add(C3);

        HBox.setHgrow(vbox_A, Priority.ALWAYS);
        HBox.setHgrow(vbox_B, Priority.ALWAYS);
        HBox.setHgrow(vbox_C, Priority.ALWAYS);

        deviceBox.getChildren().add(vbox_A);
        deviceBox.getChildren().add(vbox_B);
        deviceBox.getChildren().add(vbox_C);

    }

    private void initListView(){
        listView.getItems().addAll(
                "A","B","C"
        );
        listView.setPrefHeight(100);//3行
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView listView) {
                return new ListCell<String>(){
                    private final ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);  // 没有内容时清空图标
                            setText(null);
                            setStyle("-fx-background-color: transparent;");
                            setOnMouseEntered(event -> setStyle("-fx-background-color: transparent;"));
                            // 移除选中样式
                            selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                                if (isNowSelected) {
                                    setStyle("-fx-background-color: transparent;");
                                }
                            });

                        } else {

                            // 根据索引选择对应的图标
                            int index = getIndex();
                            if (index == 0) {
                                imageView.setImage(DEVICE_A); // 图标 A
                            } else if (index == 1) {
                                imageView.setImage(DEVICE_B); // 图标 B
                            } else {
                                imageView.setImage(DEVICE_C); // 图标 C
                            }
                            imageView.setFitWidth(20);
                            imageView.setFitHeight(20);

                            // 设置图标和文本
                            setGraphic(imageView);
                            setText(item);
                            setPrefHeight(30);
                        }
                    }
                };
            }
        });
    }




    //外部调用的更新
    public static void updateDevices(){
        //TODO: 需要获取到ABC“每一个”设备，随后一一对应到分隶属的Box中
        updateWaitingQueue();
        updateBeingUsed();
    }
    private static void updateWaitingQueue(){
        //先重置所有的ListCell为空
        appendToListCell(0, "A");
        appendToListCell(1, "B");
        appendToListCell(2, "C");

        //迭代器遍历map
        Map<String, List<Integer>> queue = DeviceManager.getInstance().getBlockedQueueMessage();
            if (queue!=null && !queue.isEmpty()){
                for (Map.Entry<String, List<Integer>> entry : queue.entrySet()) {
                    String key = entry.getKey();
                    List<Integer> value = entry.getValue();
                    // 输出调试信息
                   // System.out.println("设备: " + key + ", PID 列表: " + value);
                    if (value != null) {
                        switch (key) {
                            case "A":
                                appendToListCell(0, "A {" + value + "}");
                                break;
                            case "B":
                                appendToListCell(1, "B {" + value + "}");
                                break;
                            case "C":
                                appendToListCell(2, "C {" + value + "}");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }


    }

    private static void appendToListCell(int index, String text){
        if (index >= 0 && index < listView.getItems().size()) {
            // 更新 ListView 中的该行
            listView.getItems().set(index, text);
        } else {
            System.out.println("索引超出范围");
        }
    }

    //重置Label
        private static void reSet(){
            vbox_A.getChildren().clear();
            vbox_B.getChildren().clear();
            vbox_C.getChildren().clear();
            A1.setText("A1 [空闲]");
            A2.setText("A2 [空闲]");
            B1.setText("B1 [空闲]");
            B2.setText("B2 [空闲]");
            B3.setText("B3 [空闲]");
            C1.setText("C1 [空闲]");
            C2.setText("C2 [空闲]");
            C3.setText("C3 [空闲]");
        }


    private static void updateBeingUsed(){

        reSet();
        //迭代器遍历map
        Map<String, Integer> beingUsed = DeviceManager.getInstance().getUsedDeviceMessage();
        if (beingUsed!=null && !beingUsed.isEmpty()){
            for (Map.Entry<String, Integer> entry : beingUsed.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                    switch (key){
                        case "A1":
                            A1.setText(key + " ["+ value +"]");
                            break;
                        case "B1":
                            B1.setText(key + " ["+ value +"]");
                            break;
                        case "C1":
                            C1.setText(key + " ["+ value +"]");
                            break;
                        case "A2":
                            A2.setText(key + " ["+ value +"]");
                            break;
                        case "B2":
                            B2.setText(key + " ["+ value +"]");
                            break;
                        case "C2":
                            C2.setText(key + " ["+ value +"]");
                            break;
                        case "B3":
                            B3.setText(key + " ["+ value +"]");
                            break;
                        case "C3":
                            C3.setText(key + " ["+ value +"]");
                            break;
                        default:
                            break;
                    }

            }
        }

        vbox_A.getChildren().add(A1);
        vbox_A.getChildren().add(A2);

        vbox_B.getChildren().add(B1);
        vbox_B.getChildren().add(B2);
        vbox_B.getChildren().add(B3);

        vbox_C.getChildren().add(C1);
        vbox_C.getChildren().add(C2);
        vbox_C.getChildren().add(C3);

    }
}
