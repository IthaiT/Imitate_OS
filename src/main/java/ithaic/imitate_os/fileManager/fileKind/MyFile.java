package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import lombok.Data;

@Data
public class MyFile {

    /*TODO
     *   1. 文件属性
     *       1.1 0X80 目录
     *       1.2 0X40 可执行文件
     *       1.3 0X20 普通文件
     *   2. 初始文件/文件夹长度均为0
     * */
    @Data
    public class CatalogItem{
        private char[] CatalogItem = new char[8];
        public CatalogItem(char[] name, char extendedName, char property, char startBlock, char length){
            createCatalogItem(name, extendedName, property, startBlock, length);
        }
        /**
         * @param name:         文件名
         * @param extendedName: 扩展名
         * @param property:     文件属性
         * @param freeBlock:    起始盘块号
         * @param length:       文件大小，单位为盘块数
         */
        private void createCatalogItem(char[] name, char extendedName, char property, char freeBlock, char length){
            for (int i = 0; i < 8; i++) {
                CatalogItem[i] = (char) 0;
            }
            for (int i = 0; i < filename.length(); i++) {
                CatalogItem[i] = name[i];
            }
            CatalogItem[3] = extendedName;
            CatalogItem[4] = property;
            CatalogItem[5] = freeBlock;
            CatalogItem[6] = (char) 0;
            CatalogItem[7] = length;
        }
    }
    private String filename;
    private String[] directoryArray;
    private boolean executable = false;

      /**
     *创建文件的步骤
     * 1、获得文件名还有父目录数组
     * 2、判断父目录是否存在，检查文件名是否合法
     * 3、获得空闲盘块号(是否有空闲盘块）
     * 4、创建目录项、向磁盘写入目录项
     * 5、设置FAT表项
     * @param mixedArray: 用户创建的文件路径
     * */
    public void create(String[] mixedArray){
        handleMixedArray(mixedArray); //获得文件名还有父目录数组
        // 判断父目录是否存在
        int parentBlock = Disk.findBottomFileBlock(directoryArray);
        if(parentBlock == 0){
            return;
        }

        //判断文件名是否合法
        if(!isValidFilename(parentBlock)){
            return;
        }
        //获得空闲盘块号
        int freeBlock = Disk.getFreeBlock();
        if(freeBlock == 0){
            System.out.println("No free block available");
            return;
        }

        //设置FAT表项
        Disk.writeChar((char)1, (char)(freeBlock/64), (char)(freeBlock%64));//设置子目录的FAT表项
        // 根据是否是可执行文件，设置不同的目录项
        CatalogItem catalogItem;
        if(executable)
            catalogItem = new CatalogItem(filename.toCharArray(), 'e', (char)0x40, (char)freeBlock, (char)0);
        else
            catalogItem = new CatalogItem(filename.toCharArray(), (char)0, (char)0x20, (char)freeBlock, (char)0);
        Disk.writeCatalogItem(catalogItem.getCatalogItem(), parentBlock, freeBlock);
    }


    /**
     * 用来分离文件名和父目录数组
     * @param mixedArray: 外部传入的，既有文件名又有父目录数组的混合数组
     * */
    protected void handleMixedArray(String[] mixedArray){
        filename = mixedArray[mixedArray.length-1];
        // i从1开始，因为按照/分割，第一个是空字符串
        directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
    }


    /**
     * 判断用户创建的文件名是否合法
     * */
    protected boolean isValidFilename(int parentBlock){
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
        Disk.readBlock(parentBlock);
        char[] buffer = Disk.getReadBuffer();
        for (int i = 0; i < 8; i++) {
            if(filename.equals(new String(buffer,i*8,3))){
                System.out.println("file already exists");
                return false;
            }
        }
        return true;
    }


    /**
     * @param path: 用户要删除的文件的绝对路径
     *
     * */
    void delete(String path){

    }
}
