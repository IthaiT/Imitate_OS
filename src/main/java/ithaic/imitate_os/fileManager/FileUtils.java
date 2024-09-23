package ithaic.imitate_os.fileManager;

import java.util.Arrays;

public class FileUtils {
    /**
     * 删除文件
     * @param filePath 文件路径数组
     * */
    public static void deleteFile(String[] filePath) {
        //判断文件是否存在
        if(!isFileExists(filePath, (char) 0x20)){
            System.out.println("File not found");
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
            Disk.setFAT(0,ptr);//设置FAT表
            ptr = temp[tempPtr%64]; //获得下一页的指针
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
            System.out.println("Invalid path");
            return;
        }
        //判断目录是否存在
        if(!isFileExists(filePath, (char) 0x80)){
            System.out.println("File not found");
            return;
        }
        //得到目录块的位置
        int position = getCatalogItemPosition(filePath);
        char[] block = Disk.readBlock(position/64);//得到目录项所在的盘块
        int ptr = block[position%64 + 5];//得到分配给目录的盘块号
        char[] content = Disk.readBlock(ptr);
        for (int i = 0; i < 64; i++) {
            if(content[i]!= 0){
                System.out.println("Directory not empty");
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
    public static void typeFile(String[] filePath) {
        if(filePath.length <= 0){
            System.out.println("Invalid path");
            return;
        }
        //判断目录是否存在
        if(!isFileExists(filePath, (char) 0x20)){
            System.out.println("File not found");
            return;
        }
        int position = getCatalogItemPosition(filePath);//得到目录块的位置
        char[] block = Disk.readBlock(position/64); //得到目录块所在盘块
        int ptr = block[position%64 + 5];//得到第一页的指针
        //先判断文件是否为空
        char[] content = Disk.readBlock(ptr);
        for (int i = 0; i < 64; i++) {
            if(content[i] != 0)break;
            if(i == 63) {
                System.out.println("File is empty");
                return;
            }
        }
        //循环打印文件内容，知道文件结尾
        while(ptr != 1){
            content = Disk.readBlock(ptr);
            int length = 0;
            for (int i = 0; i < 64; i++) {
                if(content[i] != 0)length++;
                else break;
            }
            char[] buffer = new char[length];
            System.arraycopy(content, 0, buffer, 0, length);
            System.out.println(buffer);
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
     * 获得目录项在磁盘中的位置
     * @param mixedArray 文件路径数组
     * @return 目录项在磁盘中的位置
     * */
    private static int getCatalogItemPosition(String[] mixedArray) {
        String filename = mixedArray[mixedArray.length-1];
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        String[] directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        int position = Disk.findBottomFileBlock(directoryArray);
        char[] block = Disk.readBlock(position);
        for (int i = 0; i < 8; i += 8) {
            String catalogItem = new String(block, i*8, 3).trim();
            if(catalogItem.equals(filename)){
                return position* Disk.BLOCK_SIZE + i;
            }
        }
        return 0;
    }

}
