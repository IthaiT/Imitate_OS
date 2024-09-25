package ithaic.imitate_os.fileManager;

import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class FileInteract {
    private static TextField CommandInput;
    @Getter
    private static TextArea historyCommand;
    private static Button button;
    private static String command;
    private static String[] commandArray;
    @Getter
    private static ArrayList<String> currentPath = new ArrayList<>();
    private static ArrayList<String> sourceArray = new ArrayList<>();
    private static ArrayList<String> aimArray = new ArrayList<>();

    public FileInteract(TextField CommandInput, TextArea historyCommand ,Button button){
        currentPath.add("");
        new Disk();// 创建磁盘
        FileInteract.CommandInput = CommandInput;//获得用户输入
        FileInteract.historyCommand = historyCommand;//获得历史命令条
        //设置按钮鼠标监听事件
        FileInteract.button = button;
        historyCommand.appendText("ImitateOS:  " +FileUtils.getPathString(currentPath.toArray(new String[0])));
        button.setOnMouseClicked(e -> commandAction());
        button.setOnKeyPressed(e -> {
            if(e.getCode().toString().equals("ENTER")) {
                commandAction();
            }
        });
        CommandInput.setOnAction(e -> commandAction());

    }
    private void commandAction(){
        command = CommandInput.getText();
        historyCommand.appendText("$  " + command + "\n");
        HandleCommand();
        historyCommand.appendText("\n");
        historyCommand.appendText("----------------------------------------------------------------------------------\n");
        historyCommand.appendText("ImitateOS:  " +FileUtils.getPathString(currentPath.toArray(new String[0])));
        CommandInput.clear();
    }

    private void HandleCommand() {
        if(!handleCommandUtil())return;

        if (commandArray[0].equals("create")) {// 创建文件，包括普通文件和可执行文件
            MyFile myFile = new MyFile(sourceArray.toArray(new String[0]));
            if(myFile.isAvailable()){
                char[] content = new PopUpWindow().popUp();
                FileUtils.writeFile(sourceArray.toArray(new String[0]),content);
            }
        }
        else if (commandArray[0].equals("delete")) {// 删除文件
            FileUtils.deleteFile(sourceArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("type")) {// 显示文件内容
            FileUtils.typeFile(sourceArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("copy")) {// 复制文件
            FileUtils.hardCopyFile(sourceArray.toArray(new String[0]), aimArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("move")) {// 移动文件
            FileUtils.moveFile(sourceArray.toArray(new String[0]), aimArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("mkdir")) {// 创建目录
            new Directory(sourceArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("rmdir")) {// 删除目录
            FileUtils.deleteDirectory(sourceArray.toArray(new String[0]));
        }
        else if(commandArray[0].equals("deldir")){
            FileUtils.deleteAllFiles(sourceArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("format")) {// 格式化磁盘
            Disk.format();
        }
        else if (commandArray[0].equals("cd")) {// 切换目录
            FileUtils.changeDirectory(sourceArray.toArray(new String[0]));
        }
        else if (commandArray[0].equals("vi")) {// 编辑文件
            PopUpWindow popUpWindow = new PopUpWindow();

            //判断文件是否存在
            if(FileUtils.typeFile(sourceArray.toArray(new String[0]))!=null){
                popUpWindow.appendText(FileUtils.typeFile(sourceArray.toArray(new String[0])).toString());
            }
            char[] content = popUpWindow.popUp();
            FileUtils.writeFile(sourceArray.toArray(new String[0]),content);
        }
        else if (commandArray[0].equals("ls")) {// 显示目录内容
            FileUtils.listDirectory(currentPath.toArray(new String[0]));
        }
        else if (commandArray[0].equals("pwd")) {// 显示当前目录
            historyCommand.appendText(FileUtils.getPathString(currentPath.toArray(new String[0])));
        }
        else if (commandArray[0].equals("show")) {//显示磁盘信息
            for (int i = 0; i < 10; i++) {
                Disk.printBlock(i);
            }
        }
        else{
            FileInteract.getHistoryCommand().appendText("命令错误！\n");
        }

    }


    /** 处理用户输入，得到命令与路径数组
     * */
    private boolean handleCommandUtil(){
        commandArray = null;
        sourceArray.clear();
        aimArray.clear();
        commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        if(commandArray.length>3){
            FileInteract.getHistoryCommand().appendText("命令错误！\n");
            return false;
        }
        if(commandArray.length == 1){
            if(commandArray[0].equals("format") || commandArray[0].equals("ls") || commandArray[0].equals("pwd")
                    || commandArray[0].equals("show"))return true;
            else {
                FileInteract.getHistoryCommand().appendText("命令错误！\n");
                return false;
            }
        }

        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组

        //如果是相对路径，加入当前路径
        if(isRelative(directoryArray)) {
            sourceArray.addAll(currentPath);
            for (String s : directoryArray) {
                if (!s.isEmpty()) {
                    sourceArray.add(s);
                }
            }
        }
        //如果是绝对路径，直接加入目录数组
        else{
            if(!directoryArray[0].isEmpty())sourceArray.add("");
            sourceArray.addAll(Arrays.asList(directoryArray).subList(0, directoryArray.length));
        }

        directoryArray = null;
        if(commandArray.length == 3){
            directoryArray = commandArray[2].split("/"); // 以/分割文件名数组
            if(isRelative(directoryArray)){
                aimArray.addAll(currentPath);
                for (String s : directoryArray) {
                    if (!s.isEmpty()) {
                        aimArray.add(s);
                    }
                }
            }
            else{
                if(!directoryArray[0].isEmpty())aimArray.add("");
                aimArray.addAll(Arrays.asList(directoryArray).subList(0, directoryArray.length));
            }
        }
        return true;
    }


    /**
     * 判断是否是相对路径
     * @param directoryArray 目录数组
     * @return true表示是相对路径 false表示是绝对路径*/
    private boolean isRelative(String[] directoryArray){
        //如果长度为0，说明输入用户输入”/“，则是绝对路径
        if(directoryArray.length == 0)return false;
        String filename;
        if(directoryArray.length == 1)filename = directoryArray[0];
        else filename = directoryArray[1];
        char[] rootDisk = Disk.readBlock(4);
        for (int i = 0; i < 64; i+=8) {
            //如果输入的目录包含一级目录，说明是绝对路径
            if(filename.equals(new String(rootDisk, i, 3))){
                return false;
            }
        }
        return true;
    }
}

