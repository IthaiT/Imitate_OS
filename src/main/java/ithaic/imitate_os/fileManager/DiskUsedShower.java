package ithaic.imitate_os.fileManager;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DiskUsedShower {
    private static FlowPane diskUsedPane;
    static Rectangle[] rectangles=new Rectangle[256];
    private static String commandString = null; // 判断用户是否输入新的命令，以此来判断目录树是否需要更新
    private static char[] FAT = new char[256];

    private static final Color beingUsedColor = Color.rgb(100,100,100);
    private static final Color notUsedColor = Color.rgb(200,200,200);
/*255, 0, 0, 0.5);*
 * 初始化前五个1，后面全0
 * */
static {
    Arrays.fill(FAT, (char) 0);
    for(int i=0;i<5;i++){
        FAT[i]=(char) 1;
    }
}

    public DiskUsedShower(FlowPane diskUsedPane){
        DiskUsedShower.diskUsedPane = diskUsedPane;
        diskUsedPane.setHgap(3);
        diskUsedPane.setVgap(3);
        loadPanes();
    }

//    更新方法
    public static void updateDiskUsed(){
        String tmp = FileInteract.getCommand();
        if (tmp == null || tmp.equals(commandString)) return;
        commandString = tmp;
        String command = FileInteract.getCommandArray()[0];
        String[] commandArray = {"create", "delete", "copy", "move", "mkdir", "rmdir", "deldir", "format","vi"};
        for (String str : commandArray) {
            if (Objects.equals(command, str)) {
                ArrayList<Integer> state= loadDiskState();
                for (Integer integer : state) {
                    rectangles[integer].setFill((rectangles[integer].getFill().equals(notUsedColor))?beingUsedColor:notUsedColor);
                    rectangles[integer].setUserData(setRectData(rectangles[integer],integer));
                    updateTooltip(rectangles[integer]);
                }
                diskUsedPane.getChildren().clear();
                diskUsedPane.getChildren().addAll(rectangles);
                break;
            }
        }
    }
// 初始化
    private void loadPanes(){
        for (int i = 0; i < 4; i++) {
            char[] tmp = Disk.readBlock(i);
            for (int j = 0; j < 64; j++) {
                rectangles[i*64+j]=new Rectangle(10,10);
                //只要不为0设置被使用颜色
                if (tmp[j] != 0) {
                    rectangles[i*64+j].setFill(beingUsedColor);
                }
                else{
                    //磁盘为空
                    rectangles[i*64+j].setFill(notUsedColor);
                }
                rectangles[i*64+j].setUserData(setRectData(rectangles[i*64+j],i*64+j));
                updateTooltip(rectangles[i*64+j]);
                diskUsedPane.getChildren().add(rectangles[i*64+j]);
            }
        }
    }
    /**
     * 鼠标悬停会显示盘信息
     * */
    private static void updateTooltip(Rectangle rectangle){
        // 添加鼠标悬停显示信息
        Tooltip tip=new Tooltip();
        tip.setText(rectangle.getUserData().toString());
        Tooltip.install(rectangle,tip);
    }
    private static String setRectData(Rectangle rectangle, int index){
        String diskStates=rectangle.getFill().equals(beingUsedColor)?"已占用":"未占用";
        String s="盘"+index+":"+diskStates;
        return s;
    }

    /**
    * 记录这是FAT的第几个存入到数组中，返回“需要对调颜色的数组”
    * */
    private static ArrayList<Integer> loadDiskState(){
        ArrayList<Integer> state=new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            char[] tmp = Disk.readBlock(i);
            for (int j = 0; j < 64; j++) {
               if(FAT[i*64 + j]!=tmp[j]){
                   //如果二者不相等，需要记录这是FAT的第几个存入到数组中，返回“需要对调颜色的数组”
                   FAT[i*64 + j]=tmp[j];
                    state.add(i*64+j);
               }
            }
        }
        return state;
    }
}

