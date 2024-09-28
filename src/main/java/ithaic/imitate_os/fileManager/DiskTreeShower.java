package ithaic.imitate_os.fileManager;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;


public class DiskTreeShower {
    private static final Image FOLDER_IMAGE = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/directory.png")));
    private static final Image DOCUMENT_IMAGE = new Image(Objects.requireNonNull(DiskTreeShower.class.getResourceAsStream("/ithaic/imitate_os/icons/document.png")));
    private static TreeView disktree;


    public DiskTreeShower(TreeView disktree) {
        DiskTreeShower.disktree = disktree;
        TreeItem<String> root = new TreeItem<>("/");
        root.setGraphic(getIcon(FOLDER_IMAGE));
        //设置树视图的根目录
        disktree.setRoot(root);
        //循环创建子目录/子文件
        createTree(4, root);
    }


    /** 更新树结构，供外部调用
     * */
    public static void updateTree(){
        TreeItem<String> root = disktree.getRoot();
        root.getChildren().clear();
        createTree(4, root);
    }


    /**
     * 创建树结构
     * @param ptr 指向磁盘块的指针
     * @param parent 父节点
     * */
    private static void createTree(int ptr, TreeItem<String> parent) {
        char[] buffer = new char[64];
        char[] tmp = Disk.readBlock(ptr);
        for (int i = 0; i < 64; i++) {
            buffer[i] = tmp[i];
        }
        for (int i = 0; i < 8; i++) {
            if(buffer[i * 8] == 0)return;
            if(buffer[i * 8 + 4] == 0x20 || buffer[i * 8 + 4] == 0x40){
                TreeItem<String> item = new TreeItem<>(new String(buffer, i * 8, 3));
                item.setGraphic(getIcon(DOCUMENT_IMAGE));
                parent.getChildren().add(item);
                System.out.println(new String(buffer, i * 8, 3));
            }
            if(buffer[i * 8 + 4] == 0x80){
                TreeItem<String> item = new TreeItem<>(new String(buffer, i * 8, 3));
                item.setGraphic(getIcon(FOLDER_IMAGE));
                createTree(buffer[i * 8 + 5], item);
                parent.getChildren().add(item);
                System.out.println(new String(buffer, i * 8, 3));
            }
        }
    }


    /**
     * 获取图标
     * @param image 图片*/
    private static ImageView getIcon(Image image){
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }


}
