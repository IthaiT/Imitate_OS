package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;

public class Directory extends MyFile {
    public Directory(String[] mixedArray){
        super();
        create(mixedArray);
    }

    private void create(String[] mixedArray){
        //获得文件名还有父目录数组
        if(handleMixedArray(mixedArray) == false){
            setAvailable(false);
            return;
        }
        //获得父目录的盘块号
        int parentBlock = Disk.findBottomFileBlock(getDirectoryArray());
        if(parentBlock == 0){
            setAvailable(false);
            return;
        }
        //判断文件名是否合法
        if(!isValidFilename(parentBlock)){
            System.out.println("Invalid filename");
            setAvailable(false);
            return;
        }
        //获得空闲盘块号
        int allocateBlock = Disk.getFreeBlock();
        if(allocateBlock == 0){
            System.out.println("No free block available");
            setAvailable(false);
            return;
        }
        setAllocatedBlocks(allocateBlock);
        // 设置FAT
        Disk.setFAT(1, allocateBlock);
        // 创建目录项
        CatalogItem catalogItem = new CatalogItem(getFilename().toCharArray(), (char)0, (char)0x80, (char)allocateBlock, (char)0);
        Disk.writeCatalogItem(catalogItem.getCatalogItem(), parentBlock,8);
    }


    /**
     * 重写父类方法：目录文件不能写入数据
     * @param content 写入的数据
     * */
    @Override
    public boolean writeData(char[] content) {
        System.out.println("Cannot write data to a directory");
        return false;
    }

    /**
     * 重写父类方法：目录文件文件名合法标准与一般文件不同
     * @param parentBlock 父目录盘块号，用于检查是否存在同名文件夹
     * @return 合法返回true，不合法返回false
     * */
    @Override
    public boolean isValidFilename(int parentBlock){
        String filename = getFilename();
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
        //判断是否存在同名文件夹
        Disk.readBlock(parentBlock);
        char[] buffer = Disk.getReadBuffer();
        for (int i = 0; i < 8; i++) {
            if(filename.equals(new String(buffer,i*8,3)) && buffer[i*8+4] == 0x80 ){
                System.out.println("file already exists");
                return false;
            }
        }
        return true;
    }
}
