package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.fileManager.DiskTreeShower;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class DeviceUsedShower {

    /**
     * 是设备的图片/图标
     * */
    private static final Image DEVICE_A = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceA.png")));
    private static final Image DEVICE_B = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceB.png")));
    private static final Image DEVICE_C = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/deviceC.png")));
    /**
     * ABC三大类的,竖着排列的盒子
     * */
    private static VBox devicesA;
    private static VBox devicesB;
    private static VBox devicesC;
    /**
     * 各个设备的数量
     * */
    private static final int A_AMOUNT = 2;
    private static final int B_AMOUNT = 3;
    private static final int C_AMOUNT = 3;
    /**
     * Vbox里确定到每一行的HBox的表示数组,每一个元素
     * 由两部分组成,一个是设备图标,另一个是表示占用的圆
     * 圆会根据是否被占用而改变颜色,如绿色空闲,红色占用
     * */
    private static HBox[] deviceA = new HBox[A_AMOUNT];
    private static HBox[] deviceB = new HBox[B_AMOUNT];
    private static HBox[] deviceC = new HBox[C_AMOUNT];

    /**
     * 初始化一遍，把图标和圆圈放入数组的每一个元素中
     * */
    static {
        initDeviceShow(deviceA,DEVICE_A);
        initDeviceShow(deviceB,DEVICE_B);
        initDeviceShow(deviceC,DEVICE_C);
    }

    public DeviceUsedShower(VBox devicesA,VBox devicesB,VBox devicesC){
        DeviceUsedShower.devicesA = devicesA;
        DeviceUsedShower.devicesB = devicesB;
        DeviceUsedShower.devicesC = devicesC;
        //把HBox数组塞进VBox里
        devicesA.getChildren().addAll(Arrays.asList(deviceA));
        devicesB.getChildren().addAll(Arrays.asList(deviceB));
        devicesC.getChildren().addAll(Arrays.asList(deviceC));

    }

    private static void initDeviceShow(HBox[] hBoxes,Image image){
        for (HBox hbox:hBoxes){
            //设置占满
            VBox.setVgrow(hbox, Priority.ALWAYS);

            hbox.setPadding(new Insets(10));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            Canvas canvas = new Canvas(10,10);
            drawCircle(canvas, Color.GRAY);
            hbox.getChildren().addAll(imageView,canvas);
        }
    }

    //绘制一个圆
    private static void drawCircle(Canvas canvas, Color color){
        GraphicsContext gc= canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillOval(0,0,canvas.getWidth(),canvas.getHeight());
    }

    private void updateDevices(){
        //TODO: 需要获取到ABC“每一个”设备，随后一一对应到分隶属的VBox中的HBox中，对状态进行canvas颜色的更新。

        Map devicesMap = DeviceManager.getInstance().getDevices();
         devicesMap.get("A");
    }


}
