package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;

public class Directory extends MyFile {
    @Override
    public void create(String[] mixedArray){
        handleMixedArray(mixedArray); //获得文件名还有父目录数组
        //获得父目录的盘块号
        int parentBlock = Disk.findBottomFileBlock(getDirectoryArray());
        if(parentBlock == 0){
            return;
        }
        //判断文件名是否合法
        if(!isValidFilename(parentBlock)){
            System.out.println("Invalid filename");
            return;
        }
        //获得空闲盘块号
        int freeBlock = Disk.getFreeBlock();
        if(freeBlock == 0){
            System.out.println("No free block available");
            return;
        }
        // 设置FAT
        Disk.writeChar((char)1, (char)(freeBlock/64), (char)(freeBlock%64));//设置子目录的FAT表项
        // 创建目录项
        CatalogItem catalogItem = new CatalogItem(getFilename().toCharArray(), (char)0, (char)0x80, (char)freeBlock, (char)0);
        Disk.writeCatalogItem(catalogItem.getCatalogItem(), parentBlock,8);
    }
}
