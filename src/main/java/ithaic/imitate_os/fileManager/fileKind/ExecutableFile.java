package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;

public class ExecutableFile implements File {
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
        create(name, (char)0,(char)0x40,(char)startBlock, (char) 0);
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
    /*
    * TODO 指令采用两个字节
    *  1、赋值指令：OX01 0X?
    *       ?为0-255任一数字
    *  2、加1指令：OX02 0X00
    *  3、减1指令：OX03 0X00
    *  4、特殊指令：0X21 0x?
    *       0x? 0x21代表! 第一个?代表A、B、C 第二个?代表0-255任一数字（时间）
    *  5、end指令：0X05 0X00
    * */

}
