package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.fileKind.File;

public class NormalFile implements File {

    // TODO 创建目录项，写入磁盘，供外部调用
    @Override
    public void create(String fileName) {
        int startBlock = Disk.getFreeBlock();
        // 如果起始块号为0，表示没有空闲块可用
        if(startBlock == 0){
            System.out.println("No free block available");
            return;
        }
        // 如果文件名长度超过3个字符或为空，则返回
        if(fileName.length() > 3 || fileName.isEmpty()){
            System.out.println("Path too long");
            return;
        }
        // 检查文件名是否合法
        if(fileName.contains("/") || fileName.contains(".") ||fileName.contains("$")) {
            System.out.println("Invalid path");
            return;
        }
        create(name, (char)0,(char)0x20,(char)startBlock, (char) 0);
    }
    // TODO 创建目录项，写入磁盘，供内部调用
    @Override
    public void create(char[] name, char extendedName, char property, char startBlock, char length) {
        char[] fileBlock = new char[8];
        fileBlock[0] = name[0];
        fileBlock[1] = name[1];
        fileBlock[2] = name[2];
        fileBlock[3] = extendedName;
        fileBlock[4] = property;
        fileBlock[5] = startBlock;
        fileBlock[6] = length;
        fileBlock[7] = (char) 0;
        // 将文件块写入磁盘
        Disk.writeBlock(startBlock, fileBlock);
    }


    @Override
    public void delete(String path) {

    }
}
