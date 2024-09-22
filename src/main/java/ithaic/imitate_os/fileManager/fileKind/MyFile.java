package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import lombok.Data;
import lombok.NoArgsConstructor;

/*TODO
 *   1. 文件属性
 *       1.1 0X80 目录
 *       1.2 0X40 可执行文件
 *       1.3 0X20 普通文件
 *   2. 初始文件/文件夹长度均为0
 * */
@Data
@NoArgsConstructor
public class MyFile {
    @Data
    public class CatalogItem{
        private char[] CatalogItem = new char[8];//目录项内容
        public CatalogItem(char[] name, char extendedName, char property, char startBlock, char length){
            createCatalogItem(name, extendedName, property, startBlock, length);
        }
        /**
         * 封装目录项
         * @param name:         文件名
         * @param extendedName: 扩展名
         * @param property:     文件属性
         * @param freeBlock:    起始盘块号
         * @param fileLength:       文件大小，单位为盘块数
         */
        private void createCatalogItem(char[] name, char extendedName, char property, char freeBlock, char fileLength){
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
            CatalogItem[7] = fileLength;
        }
    }
    private String filename; //文件名
    private String[] directoryArray; //路径文件夹数组
    private boolean executable = false; //文件是否可执行
    private int allocatedBlocks = 0; //文件
    private int fileLength = 0; // 文件长度
    private int ItemPosition = 0; //目录项起始地址

    public MyFile(String[] mixedArray){
        create(mixedArray);
    }

    /**
     * 创建文件的步骤
     * 1、获得文件名还有父目录数组
     * 2、判断父目录是否存在，检查文件名是否合法
     * 3、获得空闲盘块号(是否有空闲盘块）
     * 4、创建目录项、向磁盘写入目录项
     * 5、设置FAT表项
     * @param mixedArray: 用户创建的文件路径
     *                  * */
    private void create(String[] mixedArray){
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
        allocatedBlocks = Disk.getFreeBlock();
        if(allocatedBlocks == 0){
            System.out.println("No free block available");
            return;
        }

        //设置FAT表项
        setFAT((char)1, allocatedBlocks);
        // 根据是否是可执行文件，设置不同的目录项
        CatalogItem catalogItem;
        if(executable)
            catalogItem = new CatalogItem(filename.toCharArray(), 'e', (char)0x40, (char)allocatedBlocks, (char)fileLength);
        else
            catalogItem = new CatalogItem(filename.toCharArray(), (char)0, (char)0x20, (char)allocatedBlocks, (char)fileLength);
        ItemPosition = Disk.writeCatalogItem(catalogItem.getCatalogItem(), parentBlock, allocatedBlocks);
    }


    /**
     * 供外部使用，可以向文件中写入字符，一次只能写入64byte
     * @param content: 要写入的内容
     * @return 是否写入成功
     * */
    public boolean writeData(char[] content){
        //先判断要写入的块是否已经被写满了
        boolean isFull = true;
        char[] buffer = Disk.readBlock(allocatedBlocks);
        for (int i = 0; i < 64; i++) {
            if(buffer[i] != (char)0)break;
            if(i == 63)isFull = false;
        }
        //如果被写满了，申请一个新的块，修改FAT
        if(isFull) {
            int previousBlocks = allocatedBlocks;
            allocatedBlocks = Disk.getFreeBlock();
            if(allocatedBlocks == 0){
                System.out.println("No free block available");
                return false;
            }
            setFAT(allocatedBlocks,previousBlocks);//更新前一个块的FAT表项，指向文件下一个位置
            setFAT(1,allocatedBlocks);//表示文件末尾
        }
        //将内容写入新的块中
        Disk.writeBlock(content, allocatedBlocks);
        //更新目录项中文件长度
        fileLength++;
        Disk.writeChar((char)fileLength,ItemPosition/64,ItemPosition%64+7);
        return true;
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
     * 设置FAT表项
     * @param content: 要写入的内容
     * @param where:   要写入的位置
     * */
    private void setFAT(int content, int where){
        Disk.writeChar((char)content, (char)(where/64), (char)(where%64));//设置子目录的FAT表项
    }


    /**
     * @param path: 用户要删除的文件的绝对路径
     *
     * */
    void delete(String path){

    }
}
