package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.FileInteract;
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
    private String filename; //文件名
    private char extendName = 0; //文件扩展名
    private char fileAttribute = 0x20; //文件属性
    private int allocatedBlocks = 0; //分配给文件或文件夹的最新磁盘号
    private int fileLength = 0; // 文件长度
    private String[] directoryArray; //路径文件夹数组
    private int ItemPosition = 0; //目录项起始地址
    private boolean available = true; //是否成功创建

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
        //获得文件名还有父目录数组
        if(handleMixedArray(mixedArray) == false){
            available = false;
            return;
        }
        // 判断父目录是否存在
        int parentBlock = Disk.findBottomFileBlock(directoryArray);
        if(parentBlock == 0){
            available = false;
            return ;
        }
        //判断文件名是否合法
        if(!isValidFilename(parentBlock)){
            available = false;
            return;
        }
        //获得空闲盘块号
        allocatedBlocks = Disk.getFreeBlock();
        if(allocatedBlocks == 0){
            available = false;
            FileInteract.getHistoryCommand().appendText("No free block available\n");
            return;
        }

        //设置FAT表项
        Disk.setFAT((char)1, allocatedBlocks);
        // 根据是否是可执行文件，设置不同的目录项
        CatalogItem catalogItem = new CatalogItem(filename.toCharArray(), extendName, fileAttribute, (char)allocatedBlocks, (char)fileLength);
        ItemPosition = Disk.writeCatalogItem(catalogItem.getCatalogItem(), parentBlock, 8);
    }


    /**
     * 供外部使用，可以向文件中写入字符，一次只能写入64byte
     * @param content: 要写入的内容
     * @return 是否写入成功
     * */
    public boolean writeData(char[] content){
        if(!available)return false;
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
                FileInteract.getHistoryCommand().appendText("No free block available\n");
                return false;
            }
            Disk.setFAT(allocatedBlocks,previousBlocks);//更新前一个块的FAT表项，指向文件下一个位置
            Disk.setFAT(1,allocatedBlocks);//表示文件末尾
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
    protected boolean handleMixedArray(String[] mixedArray){
        if(mixedArray.length<=0)return false;
        filename = mixedArray[mixedArray.length-1];
        // i从1开始，因为按照/分割，第一个是空字符串
        directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        return true;
    }


    /**
     * 判断用户创建的文件名是否合法
     * @param parentBlock: 父目录所在的盘块号,用于判断是否有同名文件
     * @return 文件名是否合法
     * */
    public boolean isValidFilename(int parentBlock){
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
            extendName = 'e';
            fileAttribute = 0x40;
        }
        // 如果文件名长度超过3个字符或为空，则返回
        if(filename.length() > 3 || filename.isEmpty()){
            FileInteract.getHistoryCommand().appendText("Path too long | Path is empty\n");
            return false;
        }
        // 检查文件名是否合法
        if(filename.contains("/") || filename.contains(".") ||filename.contains("$")) {
            FileInteract.getHistoryCommand().appendText("Invalid path\n");
            return false;
        }
        Disk.readBlock(parentBlock);
        char[] buffer = Disk.getReadBuffer();
        for (int i = 0; i < 8; i++) {
            if(filename.equals(new String(buffer,i*8,filename.length())) && fileAttribute == buffer[8*i + 4]){
                FileInteract.getHistoryCommand().appendText("file already exists\n");
                return false;
            }
        }
        return true;
    }
}
