package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;

public class Directory extends MyFile {
    @Override
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
        catalogItem.createCatalogItem(getFilename().toCharArray(), (char)0, (char)0x80, freeBlock, (char)0);
    }

}
