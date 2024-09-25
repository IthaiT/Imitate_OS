package ithaic.imitate_os.fileManager;

import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;

import java.util.ArrayList;
import java.util.Collections;

public class FileUtils {
    /**
     * 删除文件
     * @param filePath 文件路径数组
     * */
    public static void deleteFile(String[] filePath) {
        if(filePath.length <=0){
            FileInteract.getHistoryCommand().appendText("Invalid path\n");
            return;
        }
        //判断文件是否存在
        if(!isFileExists(filePath, (char) 0x20)){
            FileInteract.getHistoryCommand().appendText("File not found\n");
            return;
        }
        //得到目录块的位置
        int position = getCatalogItemPosition(filePath);
        char[] block = Disk.readBlock(position/64);
        //将文件占据的盘块清除,并将对应的FAT设置为0
        int ptr = block[position%64 + 5];//得到第一页的指针
        while(ptr != 1){
            Disk.writeBlock(new char[64], ptr); //清除文件占据的盘块
            char[] temp = Disk.readBlock(ptr/64);//获得FAT表
            int tempPtr = ptr; //暂存指针
            ptr = temp[tempPtr%64]; //获得下一页的指针
            Disk.setFAT(0,tempPtr);//设置FAT表
        }
        //将目录块清除
        Disk.modifyCatalogItem(new char[8], position/64, position%64);
        if(position%64 == 0 && position/64 != 4){
            Disk.setFAT(0,position);
        }
    }


    /**删除文件夹
     * @param filePath 文件夹路径数组
     * */
    public static void deleteDirectory(String[] filePath) {
        if(filePath.length <=0){
            FileInteract.getHistoryCommand().appendText("Invalid path\n");
            return;
        }
        //判断目录是否存在
        if(!isFileExists(filePath, (char) 0x80)){
            FileInteract.getHistoryCommand().appendText("File not found\n");
            return;
        }
        //得到目录块的位置
        int position = getCatalogItemPosition(filePath);
        char[] block = Disk.readBlock(position/64);//得到目录项所在的盘块
        int ptr = block[position%64 + 5];//得到分配给目录的盘块号
        char[] content = Disk.readBlock(ptr);
        for (int i = 0; i < 64; i++) {
            if(content[i]!= 0){
                FileInteract.getHistoryCommand().appendText("Directory not empty\n");
                return;
            }
        }
        //如果目录为空，将目录块清除，并且将目录块指向的页FAT设置为0
        //将目录块清除
        Disk.modifyCatalogItem(new char[8], position/64, position%64);
        Disk.setFAT(0,ptr);
    }


    /**
     * 打开文件，并将其内容打印到控制台
     * @param filePath 文件路径数组
     * */
    public static StringBuilder typeFile(String[] filePath) {
        if(filePath.length <= 0){
            FileInteract.getHistoryCommand().appendText("Invalid path\n");
            return null;
        }
        //判断目录是否存在
        if(!isFileExists(filePath, (char) 0x20)){
            FileInteract.getHistoryCommand().appendText("File not found\n");
            return null;
        }
        int position = getCatalogItemPosition(filePath);//得到目录块的位置
        char[] block = Disk.readBlock(position/64); //得到目录块所在盘块
        int ptr = block[position%64 + 5];//得到第一页的指针
        //先判断文件是否为空
        char[] content = Disk.readBlock(ptr);
        for (int i = 0; i < 64; i++) {
            if(content[i] != 0)break;
            if(i == 63) {
                FileInteract.getHistoryCommand().appendText("File is empty\n");
                return null;
            }
        }
        //循环打印文件内容，直到文件结尾
        StringBuilder sb = new StringBuilder();
        while(ptr != 1){
            content = Disk.readBlock(ptr);
            for (int i = 0; i < 64; i++) {
                if(content[i] != 0){
                    sb.append(content[i]);
                }
                else break;
            }
            char[] temp = Disk.readBlock(ptr/64);//获得FAT表
            ptr = temp[ptr%64]; //获得下一页的指针
        }
        FileInteract.getHistoryCommand().appendText(sb+ "\n");
        return sb;
    }


    /**
     * 软链接文件，仅复制项目块，不复制文件内容
     * @param sourceMixedArray 源文件路径数组
     * @param targetMixedArray 目标文件路径数组
     * */
    public static void softCopyFile(String[] sourceMixedArray, String[] targetMixedArray) {
        if(!copyFileUtil(sourceMixedArray, targetMixedArray)){
            FileInteract.getHistoryCommand().appendText("复制失败\n");
            return;
        }
        //修改目录项
        String targetFilename = targetMixedArray[targetMixedArray.length-1];
        int sourcePosition = getCatalogItemPosition(sourceMixedArray);//得到目录块的位置
        char[] block = Disk.readBlock(sourcePosition/64);//得到目录块所在盘块
        char[] content = new char[8];//得到原来目录项
        //创建新的目录项
        for (int i = 3; i < 8; i++) {
            content[i] = block[sourcePosition%64 + i];
        }
        for (int i = 0; i < targetFilename.length(); i++) {
            content[i] = targetFilename.charAt(i);
        }
        //设置新的目录项
        String[] directoryArray = new String[targetMixedArray.length-2];
        for (int i = 1; i < targetMixedArray.length - 1; i++) {
            directoryArray[i - 1] = targetMixedArray[i];
        }
        int position = Disk.findBottomFileBlock(directoryArray);
        FileInteract.getHistoryCommand().appendText(String.valueOf(position) + "\n");
        Disk.writeCatalogItem(content, position, 8);
    }


    /**
     * 硬链接文件，复制文件块与文件内容
     * @param sourceMixedArray 源文件路径数组
     * @param targetMixedArray 目标文件路径数组
     * */
    public static void hardCopyFile(String[] sourceMixedArray, String[] targetMixedArray) {
        if(!copyFileUtil(sourceMixedArray, targetMixedArray)){
            FileInteract.getHistoryCommand().appendText("复制失败\n");
            return;
        }
        //创建新文件
        MyFile targetFile = new MyFile(targetMixedArray);
        //复制文件内容
        int sourcePosition = getCatalogItemPosition(sourceMixedArray);//得到目录块的位置
        char[] block = Disk.readBlock(sourcePosition/64);//得到目录块所在盘块
        int ptr = block[sourcePosition%64 + 5];//得到第一页的指针
        while(ptr != 1){
            char[] content = new char[64];
            for (int i = 0; i < 64; i++) {
                content[i] = Disk.readBlock(ptr)[i];
            }
            targetFile.writeData(content);
            char[] temp = Disk.readBlock(ptr/64);//获得FAT表
            ptr = temp[ptr%64]; //获得下一页的指针
        }
    }


    /**
     * 判断文件/文件夹是否存在
     * @param mixedArray 文件路径数组
     * @return true:存在 false:不存在*/
    public static boolean isFileExists(String[] mixedArray, char property) {
        if(mixedArray.length <= 0 )return false;
        if(mixedArray.length == 1 && mixedArray[0].isEmpty())return true;
        String filename = mixedArray[mixedArray.length-1];
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
            property = 0x40;
        }
        // i从1开始，因为按照/分割，第一个是空字符串
        String[] directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        if(Disk.findBottomFileBlock(directoryArray,filename,property)==0)
            return false;
        return true;
    }


    /**
     * 移动文件，将文件从源路径移动到目标路径
     * @param sourceMixedArray 源文件路径数组
     * @param targetMixedArray 目标文件路径数组
     * */
    public static void moveFile(String[] sourceMixedArray, String[] targetMixedArray) {
        softCopyFile(sourceMixedArray, targetMixedArray);
        int sourcePosition = getCatalogItemPosition(sourceMixedArray);//得到目录块的位置
        Disk.modifyCatalogItem(new char[8], sourcePosition/64, sourcePosition%64);//清除原先目录项
    }


    /** 删除目录下所有文件
     * @param directoryArray 目录路径数组
     * */
    public static void deleteAllFiles(String[] directoryArray) {
        if(!isFileExists(directoryArray, (char) 0x80)){
            FileInteract.getHistoryCommand().appendText("目录不存在\n");
            return;
        }
        int position = getCatalogItemPosition(directoryArray);//得到目录块的位置
        char[] block = Disk.readBlock(position/64);//得到目录块所在盘块
        if(block[position%64 + 4] != 0x80){
            FileInteract.getHistoryCommand().appendText("不是目录\n");
            return;
        }
        int ptr = block[position%64 + 5];//得到第一页的指针
        Disk.modifyCatalogItem(new char[8], position/64, position%64);
        deleteAllFilesUtil(ptr);
    }


    /** 改变当前工作目录
     * @param directoryArray 目录路径数组*/

    public static void changeDirectory(String[] directoryArray) {
        ArrayList<String> currentPath= FileInteract.getCurrentPath();
        if(directoryArray[directoryArray.length-1].equals("..")){
            currentPath.remove(currentPath.size()-1);
        }
        if(isFileExists(directoryArray, (char) 0x80)){
            currentPath.clear();
            Collections.addAll(currentPath, directoryArray);
        }
    }


    /** 往文件中写入内容
     * @param filePath*/
    public static void writeFile(String[] filePath, char[] content) {
        //首先判断是否是目录
        if(isFileExists(filePath, (char) 0x80)){
            FileInteract.getHistoryCommand().appendText("不能写入目录\n");
            return;
        }
        //FileInteract.getHistoryCommand().appendText("安全的文件不存在（无bug）\n");
        //判断文件是否存在
        if(!isFileExists(filePath, (char) 0x20)){
            FileInteract.getHistoryCommand().appendText("被写文件不存在\n");
            return;
        }
//        int isExecutable = 0;
//        if(filePath[filePath.length-1].endsWith(".e")) isExecutable = 1;
        //判断文件内容有没有更改，有的话更新文件

        //更新文件
        clearFileContent(filePath);
        int fileLength = 0;
        int position = getCatalogItemPosition(filePath);//得到目录块的位置
        char[] block = Disk.readBlock(position/64);//得到目录块所在盘块
        int ptr = block[position%64 + 5];//得到第一页的指针,第一页一直分配给文件，不会再更新中被清除

        int pageNum = content.length/64;
        int tmpPtr = ptr;
        for (int i = 0; i < pageNum; i++) {
            char[] temp = new char[64];
            for (int j = 0; j < 64; j++) {
                temp[j] = content[i * 64 + j];
            }
            Disk.writeBlock(temp, ptr);
            tmpPtr = ptr;
            if(i<pageNum-1){
                ptr = Disk.getFreeBlock ();
                Disk.setFAT(ptr,tmpPtr);
            }
            fileLength++;
        }
        Disk.setFAT(1,tmpPtr);
        if(content.length % 64 != 0){
            if(pageNum != 0) {
                ptr = Disk.getFreeBlock();
                Disk.setFAT(ptr,tmpPtr);
            }
            char[] tmp = new char[content.length % 64];
            for (int i = 0; i < content.length % 64; i++) {
                tmp[i] = content[content.length - content.length % 64 + i];
            }
            Disk.writeBlock(tmp, ptr);
            Disk.setFAT(1,ptr);
            fileLength++;
        }

        //更新目录项中文件长度
        Disk.writeChar((char)fileLength,position/64,position%64+7);
    }


    /** 显示当前文件夹下的所有文件/文件夹
     * @param currentPath 目录路径数组*/
    public static void listDirectory(String[] currentPath) {
        int position = getCatalogItemPosition(currentPath);//得到目录块的位置
        char[] block = Disk.readBlock(position/64);//得到目录块所在盘块
        int ptr = block[position%64 + 5];//得到第一页的指针
        if(position == 4 * Disk.BLOCK_SIZE)ptr = 4;
        char[] content = Disk.readBlock(ptr);
        for(int i = 0; i < 8; i++){
            if(content[i * 8] != 0){
                String name = new String(content, i * 8, 3).trim();
                if(content[i * 8 + 4] == 0x20)
                    FileInteract.getHistoryCommand().appendText(name + "   ");
                else if (content[i * 8 + 4] == 0x40) {
                    FileInteract.getHistoryCommand().appendText(name + ".e   ");
                }
                else{
                    FileInteract.getHistoryCommand().appendText(name + "/   ");
                }
            }
        }
        FileInteract.getHistoryCommand().appendText("\n");
    }

    /** 显示当前工作路径
     * @param currentPath 目录路径数组
     * @return 工作路径字符串*/
    public static String getPathString(String[] currentPath) {
        StringBuilder sb = new StringBuilder();
        for(String path:currentPath){
            sb.append(path).append("/");
        }
        sb.append("\n");
        return sb.toString();
    }


    /**
     * 获得目录项在磁盘中的位置
     * @param mixedArray 文件路径数组
     * @return 目录项在磁盘中的位置
     * */
    private static int getCatalogItemPosition(String[] mixedArray) {
        String filename = mixedArray[mixedArray.length-1];
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        if(mixedArray.length == 1)return 4*Disk.BLOCK_SIZE;
        String[] directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        int position = Disk.findBottomFileBlock(directoryArray);
        char[] block = Disk.readBlock(position);
        for (int i = 0; i < 64; i += 8) {
            String catalogItem = new String(block, i, 3).trim();
            if(catalogItem.equals(filename)){
                return position* Disk.BLOCK_SIZE + i;
            }
        }
        return 0;
    }


    /**
     * 文件复制的工具函数，用于文件合法性判断
     * @param sourceMixedArray 源文件路径数组
     * @param targetMixedArray 目标文件路径数组
     * */
    private static boolean copyFileUtil(String[] sourceMixedArray, String[] targetMixedArray) {
        //判断传入参数是否合法
        if(sourceMixedArray.length <= 0 || targetMixedArray == null || targetMixedArray.length <= 0){
            FileInteract.getHistoryCommand().appendText("Invalid path\n");
            return false;
        }
        //判断源文件是否存在
        if(!isFileExists(sourceMixedArray, (char) 0x20)){
            FileInteract.getHistoryCommand().appendText("File not found\n");
            return false;
        }
        //判断目标路径是否存在
        String[] directoryArray = new String[targetMixedArray.length-1];
        for (int i = 0; i < targetMixedArray.length - 1; i++) {
            directoryArray[i] = targetMixedArray[i];
        }
        if(!isFileExists(directoryArray, (char) 0x80)){
            for(int i = 1; i < directoryArray.length; i++){
                if(directoryArray[i].length() > 3 || directoryArray[i].length() <= 0 || directoryArray[i].contains("$") || directoryArray[i].contains(".")){
                    FileInteract.getHistoryCommand().appendText("目标路径错误\n");
                    return false;
                }
            }
            //创建目标目录
            createDirectory(directoryArray);
        }
        //判断文件类型是否匹配
        String sourceFilename = sourceMixedArray[sourceMixedArray.length-1];
        String targetFilename = targetMixedArray[targetMixedArray.length-1];
        if(sourceFilename.endsWith(".e")){
            if(!targetFilename.endsWith(".e")){
                FileInteract.getHistoryCommand().appendText("文件类型不匹配\n");
                return false;
            }
            sourceFilename = sourceFilename.substring(0, sourceFilename.lastIndexOf("."));
            targetFilename = targetFilename.substring(0, targetFilename.lastIndexOf("."));
        }
        //判断目标文件名是否正确
        if(targetFilename.length() > 3 || targetFilename.length() <= 0){
            FileInteract.getHistoryCommand().appendText("文件名错误\n");
            return false;
        }
        return true;
    }

    /**
     * 创建多级目录
     * @param directoryArray 目录路径数组,第一个元素为空
     * */
    private static void createDirectory(String[] directoryArray) {
        String[] temp = new String[directoryArray.length - 1];
        for (int i = 1; i < directoryArray.length; i++) {
            temp[i - 1] = directoryArray[i];
        }
        for (int i = 0; i < temp.length; i++) {
            //如果存在
            String[] newDirectoryArray = new String[i + 2];
            newDirectoryArray[0] = "";
            for(int j = 1; j < newDirectoryArray.length; j++){
                newDirectoryArray[j] = temp[j-1];
            }
            if(isFileExists(newDirectoryArray, (char) 0x80)){
                continue;
            }
            //不存在，创建目录项
            new Directory(newDirectoryArray);
        }
    }


    /**
     * 删除目录下所有文件的工具函数
     * @param ptr 目录项所在的盘块号
     * */
    private static void deleteAllFilesUtil(int ptr) {
        char[] content = new char[64];
        for (int i = 0; i < 64; i++) {
            content[i] = Disk.readBlock(ptr)[i];
        }
        int storePtr = ptr;
        for(int i = 0; i < 8; i++){
            if(content[i * 8] != 0){
                if(content[i * 8 + 4] == 0x20 || content[i * 8 + 4] == 0x40){
                    ptr = content[i * 8 + 5];
                    Disk.setFAT(0,ptr);//设置FAT表
                    Disk.writeBlock(new char[64], ptr);
                }
                if(content[i * 8 + 4] == 0x80){
                    ptr = content[i * 8 + 5];
                    deleteAllFilesUtil(ptr);
                }
            }
        }
        Disk.setFAT(0,storePtr);
        Disk.writeBlock(new char[64], storePtr);
    }


    /** 清除文件内容,是写入函数的工具函数
     * @param filePath */
    private static void clearFileContent(String[] filePath) {
        int position = getCatalogItemPosition(filePath);//得到目录块的位置
        char[] block = Disk.readBlock(position/64);//得到目录块所在盘块
        int ptr = block[position%64 + 5];//得到第一页的指针,第一页一直分配给文件，不会再更新中被清除
        while(ptr != 1){
            Disk.writeBlock(new char[64], ptr);
            char[] temp = Disk.readBlock(ptr/64);//获得FAT表
            int tempPtr = ptr;
            ptr = temp[tempPtr%64]; //获得下一页的指针
            Disk.setFAT(0,tempPtr);//设置FAT表
        }
        block = Disk.readBlock(position/64);//得到目录块所在盘块
        ptr = block[position%64 + 5];//得到第一页的指针,第一页一直分配给文件，不会再更新中被清除
        Disk.setFAT(1,ptr);
    }

}
