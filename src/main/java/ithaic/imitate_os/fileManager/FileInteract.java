package ithaic.imitate_os.fileManager;

import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.ExecutableFile;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import ithaic.imitate_os.fileManager.fileKind.NormalFile;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.util.Arrays;

/*
*  TODO 此类实现用户与文件系统的交互
* */
public class FileInteract {
    private static TextField CommandInput;
    private static Button button;
    private String command;

    public FileInteract(TextField CommandInput, Button button){
        new Disk();// 创建磁盘
        FileInteract.CommandInput = CommandInput;//获得用户输入
        //设置按钮鼠标监听事件
        FileInteract.button = button;
        button.setOnMouseClicked(e -> {
            command = CommandInput.getText();
            HandleCommand();
        });
    }

    private void HandleCommand() {
        String[] commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组

        if (commandArray[0].equals("create")) {// 创建文件，包括普通文件和可执行文件
            MyFile file = new MyFile(directoryArray);
            //等待写入数据
        }
        else if (commandArray[0].equals("delete")) {// 删除文件
            FileUtils.deleteFile(directoryArray);
        }
        else if (commandArray[0].equals("type")) {// 显示文件内容
            FileUtils.typeFile(directoryArray);
        }
        else if (commandArray[0].equals("copy")) {// 复制文件

        }
        else if (commandArray[0].equals("mkdir")) {// 创建目录
            Directory directory = new Directory(directoryArray);
        }
        else if (commandArray[0].equals("rmdir")) {// 删除目录

        }
        else{
            System.out.println("命令错误！");
        }
    }
}
