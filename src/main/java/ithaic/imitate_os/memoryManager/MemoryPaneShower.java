package ithaic.imitate_os.memoryManager;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashMap;
import java.util.Random;

public class MemoryPaneShower {
    private static FlowPane flowPane;
    private static final int SIZE=512;
    private static final int CELL_SIZE=5;
    private static final Color notUsedColor = Color.rgb(200, 200, 200);
    private static final HashMap<Integer, Integer> hashMap = new HashMap<>();
    static HashMap<Integer, Color> colors = new HashMap<>();
    static Rectangle[] rectangles = new Rectangle[SIZE];
    static {
        //限定key=-1不存在的内存值，保证不会随机到这个颜色
        colors.put(-1, notUsedColor);
        for (int i = 0; i < SIZE; i++) {
            rectangles[i] = new Rectangle(CELL_SIZE, CELL_SIZE);
            rectangles[i].setFill(notUsedColor);
        }
    }
    /**
     * 获取box，初始化小矩形
     */
    public MemoryPaneShower(FlowPane flowPane) {
        MemoryPaneShower.flowPane = flowPane;
        flowPane.setPadding(new Insets(5));
        flowPane.setHgap(0);
        flowPane.setVgap(0);
        getMemoryBlocks();
        for (int i = 0; i < SIZE; i++) {
            flowPane.getChildren().add(rectangles[i]);
        }

    }
    /**
     * 读取内存块,分配颜色
     */
    private static void getMemoryBlocks() {
        MemoryBlock blocks = Memory.getInstance().getMemoryBlock();
        if (blocks == null && !hashMap.isEmpty()){
            hashMap.clear();
            colors.clear();
            colors.put(-1,notUsedColor);
        }
        while (blocks != null) {
            //非空
            if (!blocks.isFree()) {
                int start = blocks.getAddress();
                int end = blocks.getSize();
                //如果这个位置存在，key和value相同
                boolean flag = false;
                if (hashMap.containsKey(start) && hashMap.get(start) == end) {
                    //说明这个位置仍然站位
                    for (int i = start; i < end + start; i++) {
                        rectangles[i].setFill(colors.get(start));
                    }
                } else if (hashMap.containsKey(start) && hashMap.get(start) != end) {
                    hashMap.remove(start);
                    hashMap.put(start, end);
                    flag = true;
                } else {
                    hashMap.put(start, end);
                    flag = true;
                }
                if (flag) {
                    colors.remove(start);
                    Color color = createBeingUsedColor(start);
                    colors.put(start, color);
                    for (int i = start; i < end + start; i++) {
                        rectangles[i].setFill(color);
                    }
                }
            }
            blocks = blocks.getNext();
        }
    }
    //随机生成指定范围的颜色
    private static Color createBeingUsedColor(int key) {
        Color newColor;
        Random random = new Random();
        do {
            int r = random.nextInt(128); // 红色范围 0-127
            int g = random.nextInt(128); // 绿色范围 0-127
            int b = 128 + random.nextInt(128); // 蓝色范围 128-255
            newColor = Color.rgb(r, g, b);
        } while (colors.containsValue(newColor)); // 确保不重复
        colors.put(key, newColor); // 保存新颜色
        return newColor; // 返回新颜色
    }

    //    外部调用的更新方法
    public static void updateMemoryPane() {
        flowPane.getChildren().clear();
        for (Rectangle r : rectangles) {
            r.setFill(notUsedColor);
        }
        getMemoryBlocks();
        for (int i = 0; i < SIZE; i++) {
            flowPane.getChildren().add(rectangles[i]);
        }
    }
}
