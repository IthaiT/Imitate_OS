package ithaic.imitate_os.fileManager;

import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class FileInteract {
    @Getter
    private static TextField CommandInput;
    @Getter
    private static TextArea historyCommand;
    private static Button button;
    @Getter
    private static String command;
    @Getter
    private static String[] commandArray;
    @Getter
    private static ArrayList<String> currentPath = new ArrayList<>();
    private static ArrayList<String> sourceArray = new ArrayList<>();
    private static ArrayList<String> aimArray = new ArrayList<>();
    private static HistoryCommand historyCommandList;


    public FileInteract(TextField CommandInput, TextArea historyCommand, Button button) {
        currentPath.add("");
        new Disk();// 创建磁盘
        FileInteract.CommandInput = CommandInput;//获得用户输入
        FileInteract.historyCommand = historyCommand;//获得历史命令条
        //设置按钮鼠标监听事件
        FileInteract.button = button;
        historyCommand.appendText("ImitateOS:  " + FileUtils.getPathString(currentPath.toArray(new String[0])));
        historyCommandList = HistoryCommand.getInstance();
        historyCommand.requestFocus();
        //设置历史命令上下选择，联想输入
        ContextInput(historyCommandList);


        button.setOnMouseClicked(e -> commandAction());
        button.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                commandAction();
            }
        });
        CommandInput.setOnAction(e -> commandAction());

    }

    private void commandAction() {
        command = CommandInput.getText();
        historyCommand.appendText("$  " + command + "\n");
        historyCommandList.addCommand(command);
        HandleCommand();
        if (!command.equals("clear"))
            historyCommand.appendText("\n");
        historyCommand.appendText("ImitateOS:  " + FileUtils.getPathString(currentPath.toArray(new String[0])));
        CommandInput.clear();
    }

    private void HandleCommand() {
        if (!handleCommandUtil()) return;

        if (commandArray[0].equals("create")) {// 创建文件，包括普通文件和可执行文件
            MyFile myFile = new MyFile(sourceArray.toArray(new String[0]));
            if (myFile.isAvailable()) {
                char[] content = new PopUpWindow().popUp();
                FileUtils.writeFile(sourceArray.toArray(new String[0]), content);
            }
        } else if (commandArray[0].equals("delete")) {// 删除文件
            FileUtils.deleteFile(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("type")) {// 显示文件内容
            FileUtils.typeFile(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("copy")) {// 复制文件
            FileUtils.hardCopyFile(sourceArray.toArray(new String[0]), aimArray.toArray(new String[0]));
        } else if (commandArray[0].equals("move")) {// 移动文件
            FileUtils.moveFile(sourceArray.toArray(new String[0]), aimArray.toArray(new String[0]));
        } else if (commandArray[0].equals("mkdir")) {// 创建目录
            new Directory(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("rmdir")) {// 删除目录
            FileUtils.deleteDirectory(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("deldir")) {
            FileUtils.deleteAllFiles(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("format")) {// 格式化磁盘
            Disk.format();
        } else if (commandArray[0].equals("cd")) {// 切换目录
            FileUtils.changeDirectory(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("vi")) {// 编辑文件
            PopUpWindow popUpWindow = new PopUpWindow();
            //判断文件是否存在
            if (!FileUtils.isFileExists(sourceArray.toArray(new String[0]), (char) 0x20)) {
                FileInteract.getHistoryCommand().appendText("File not found\n");
                return;
            }
            StringBuilder sb = FileUtils.getFileContent(sourceArray.toArray(new String[0]));
            if (sb != null) {
                popUpWindow.appendText(sb.toString());
            }
            char[] content = popUpWindow.popUp();
            FileUtils.writeFile(sourceArray.toArray(new String[0]), content);
        } else if (commandArray[0].equals("ls")) {// 显示目录内容
            FileUtils.listDirectory(currentPath.toArray(new String[0]));
        } else if (commandArray[0].equals("pwd")) {// 显示当前目录
            historyCommand.appendText(FileUtils.getPathString(currentPath.toArray(new String[0])));
        } else if (commandArray[0].equals("show")) {//显示磁盘信息
            for (int i = 0; i < 10; i++) {
                Disk.printBlock(i);
            }
        } else if (commandArray[0].equals("exec")) {  //执行可执行文件
            FileUtils.executeFile(sourceArray.toArray(new String[0]));
        } else if (commandArray[0].equals("clear")) {
            historyCommand.clear();
        } else if (commandArray[0].equals("help")) {  //退出系统
            FileInteract.getHistoryCommand().appendText("""
                    命令列表：
                    create [文件名] [内容] 创建文件，包括普通文件和可执行文件
                    delete [文件名] 删除文件
                    type [文件名] 显示文件内容
                    copy [源文件名] [目标文件名] 复制文件
                    move [源文件名] [目标文件名] 移动文件
                    mkdir [目录名] 创建目录
                    rmdir [目录名] 删除空目录
                    deldir [目录名] 删除目录下所有文件
                    format 格式化磁盘
                    cd [目录名] 切换目录
                    vi [文件名] 编辑文件
                    ls 显示目录内容
                    pwd 显示当前目录
                    show 显示磁盘信息
                    exec [可执行文件名] 执行可执行文件
                    help 显示命令列表
                    clear 清空历史命令
                    """);

        } else {
            FileInteract.getHistoryCommand().appendText("命令错误！\n");
        }
    }


    /**
     * 处理用户输入，得到命令与路径数组
     */
    private boolean handleCommandUtil() {
        commandArray = null;
        sourceArray.clear();
        aimArray.clear();
        commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        if (commandArray.length > 3) {
            FileInteract.getHistoryCommand().appendText("命令错误！\n");
            return false;
        }
        if (commandArray.length == 1) {
            if (commandArray[0].equals("format") || commandArray[0].equals("ls") || commandArray[0].equals("pwd")
                    || commandArray[0].equals("show") || commandArray[0].equals("help") || commandArray[0].equals("clear"))
                return true;
            else {
                FileInteract.getHistoryCommand().appendText("命令错误！\n");
                return false;
            }
        }

        //处理第一个参数，即源文件或目录
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        //如果是相对路径，加入当前路径
        if (isRelative()) {
            sourceArray.addAll(currentPath);
            for (String s : directoryArray) {
                if (!s.isEmpty()) {
                    sourceArray.add(s);
                }
            }
        }
        //如果是绝对路径，直接加入目录数组
        else {
            if (directoryArray.length == 0 || !directoryArray[0].isEmpty()) sourceArray.add("");
            sourceArray.addAll(Arrays.asList(directoryArray).subList(0, directoryArray.length));
        }

        //处理第二个参数，即目标文件或目录
        if (commandArray.length == 3) {
            directoryArray = commandArray[2].split("/"); // 以/分割文件名数组
            if (isRelative()) {
                aimArray.addAll(currentPath);
                for (String s : directoryArray) {
                    if (!s.isEmpty()) {
                        aimArray.add(s);
                    }
                }
            } else {
                if (!directoryArray[0].isEmpty()) aimArray.add("");
                aimArray.addAll(Arrays.asList(directoryArray).subList(0, directoryArray.length));
            }
        }
        return true;
    }


    /**
     * 判断是否是相对路径
     *
     * @return true表示是相对路径 false表示是绝对路径
     */
    private boolean isRelative() {
        return !commandArray[1].startsWith("/");
    }


    /**
     * 获得输入框的内容，给予提示。
     * 输入框没有内容时，↑↓选择历史命令
     * 有内容时，联想输入
     */
    private void ContextInput(HistoryCommand historyCommandList) {
        ContextMenu contextMenu = new ContextMenu();
        List<String> suggestions = getStringList();
//        int[] suggestionIndex = {-1}; // 用于追踪Tab键的选择项

        // 监听输入框文本
        CommandInput.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                List<String> filteredSuggestions = new ArrayList<>();
                for (String suggestion : suggestions) {
                    // 内容非空时，快速匹配临近词条
                    if (suggestion.toLowerCase().startsWith(newText.toLowerCase())) {
                        filteredSuggestions.add(suggestion);
                    }
                }

                contextMenu.getItems().clear();
//                suggestionIndex[0] = -1; // 重置Tab选择项

                // 更新候选词
                if (!filteredSuggestions.isEmpty()) {
                    for (String suggestion : filteredSuggestions) {
                        MenuItem item = new MenuItem(suggestion);
                        item.setOnAction(e -> {
                            CommandInput.setText(suggestion);
                            CommandInput.positionCaret(suggestion.length());
                        });
                        contextMenu.getItems().add(item);
                    }
                } else {
                    contextMenu.hide();
                }
            } else {
                contextMenu.hide();
            }
        });

        // 监听键盘事件，处理上下键切换历史命令或Tab键的自动补全
        CommandInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                contextMenu.show(CommandInput, Side.BOTTOM, 0, 0);
            }
            // 输入框为空，使用上下键切换历史命令
            if (e.getCode() == KeyCode.UP) {
                CommandInput.setText(historyCommandList.getCommand(0));
                if (CommandInput.getText() != null) {
                    CommandInput.positionCaret(CommandInput.getText().length());
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                CommandInput.setText(historyCommandList.getCommand(1));
                if (CommandInput.getText() != null) {
                    CommandInput.positionCaret(CommandInput.getText().length());
                }
            }
//            // 输入框不为空，使用Tab键选择候选词
//            if (e.getCode() == KeyCode.TAB && contextMenu.isShowing()) {
//                System.out.println("Tab pressed");
//                List<MenuItem> items = contextMenu.getItems();
//                if (!items.isEmpty()) {
//                    suggestionIndex[0] = (suggestionIndex[0] + 1) % items.size(); // 循环选择
//                    MenuItem selectedItem = items.get(suggestionIndex[0]);
//                    CommandInput.setText(selectedItem.getText());
//                    CommandInput.positionCaret(selectedItem.getText().length());
//                }
//                e.consume(); // 阻止Tab键的默认行为
//            }
        });

        CommandInput.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                contextMenu.hide();
            }
        });

    }


    private static List<String> getStringList() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("create");
        suggestions.add("type");
        suggestions.add("copy");
        suggestions.add("move");
        suggestions.add("mkdir");
        suggestions.add("rmdir");
        suggestions.add("deldir");
        suggestions.add("format-are u sure ?");
        suggestions.add("cd");
        suggestions.add("vi");
        suggestions.add("ls");
        suggestions.add("pwd");
        suggestions.add("show");
        suggestions.add("exec");
        suggestions.add("help");
        suggestions.add("clear");
        return suggestions;
    }

}

