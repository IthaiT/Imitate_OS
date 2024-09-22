package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import javafx.scene.control.Dialog;
import lombok.Data;

@Data
public class MyFile {

    /*TODO
     *   1. 文件属性
     *       1.1 0X80 目录
     *       1.2 0X40 可执行文件
     *       1.3 0X20 普通文件
     * */
    public class CatalogItem{
        private char[] name = new char[3];// 文件名
        private char extendedName = 0;// 扩展名
        private char property = 0;// 文件属性
        private char startBlock = 0;// 起始盘块号
        private char length = 0;// 文件大小，单位为盘块数

        /**
         * @param name: 文件名
         * @param extendedName: 扩展名
         * @param property: 文件属性
         * @param freeBlock: 起始盘块号
         * @param length: 文件大小，单位为盘块数
         *
         * */
        void createCatalogItem(char[] name, char extendedName, char property, char freeBlock, char length){
            char[] fileBlock = new char[8];
            fileBlock[0] = name[0];
            fileBlock[1] = name[1];
            fileBlock[2] = name[2];
            fileBlock[3] = extendedName;
            fileBlock[4] = property;
            fileBlock[5] = freeBlock;
            fileBlock[6] = length;
            fileBlock[7] = (char) 0;
            // 将文件块写入磁盘
            Disk.writeBlock(fileBlock, freeBlock,8);
        }
    }
    private String filename;
    private String[] directoryArray;
    private boolean executable = false;

      /**
     *创建文件的步骤
     * 1、获得文件名还有父目录数组
     * 2、检查文件名是否合法
     * 3、获得空闲盘块号，获得最底层父目录盘块号
     * 4、创建目录项、向磁盘写入目录项
     * 5、设置FAT表项
     * @param mixedArray: 用户创建的文件路径
     * */
    public void create(String[] mixedArray){
        handleMixedArray(mixedArray); //获得文件名还有父目录数组
        //判断文件名是否合法
        if(isValidFilename() == false){
            System.out.println("Invalid filename");
            return;
        }
        //获得空闲盘块号
        char freeBlock = Disk.getFreeBlock();
        if(freeBlock == 0){
            System.out.println("No free block available");
            return;
        }
        // 设置FAT
        setFAT(freeBlock, Disk.findBottomFileBlock(getDirectoryArray()));
        // 创建目录项
        CatalogItem catalogItem = new CatalogItem();
        // 根据是否是可执行文件，设置不同的目录项
        if(executable)
            catalogItem.createCatalogItem(filename.toCharArray(), 'e', (char)0x40, freeBlock, (char)0);
        else
            catalogItem.createCatalogItem(filename.toCharArray(), (char)0, (char)0x20, freeBlock, (char)0);
    }


    /**
     * 用来分离文件名和父目录数组
     * @param mixedArray: 外部传入的，既有文件名又有父目录数组的混合数组
     * */
    protected void handleMixedArray(String[] mixedArray){
        filename = mixedArray[mixedArray.length-1];
        // i从1开始，因为按照/分割，第一个是空字符串
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
    }


    /**
     * 判断用户创建的文件名是否合法
     * */
    protected boolean isValidFilename(){
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
            executable = true;
        }
        // 如果文件名长度超过3个字符或为空，则返回
        if(filename.length() > 3 || filename.isEmpty()){
            System.out.println("Path too long");
            return false;
        }
        // 检查文件名是否合法
        if(filename.contains("/") || filename.contains(".") ||filename.contains("$")) {
            System.out.println("Invalid path");
            return false;
        }
        return true;
    }


    /**
     * 给定子目录的盘块号和父目录的盘块号，设置子目录的FAT表项和父目录的FAT表项
     * @param childBlock: 子目录的盘块号
     * @param parentBlock: 父目录的盘块号
     * */
    protected void setFAT(char childBlock, char parentBlock){
        Disk.writeBlock(childBlock, parentBlock/64, parentBlock%64) ;//设置父目录的FAT表项
        Disk.writeBlock((char)1,childBlock/64,childBlock%64);//设置子目录的FAT表项
    }


    /**
     * @param path: 用户要删除的文件的绝对路径
     *
     * */
    void delete(String path){

    }
}
