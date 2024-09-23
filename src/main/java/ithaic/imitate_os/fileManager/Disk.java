package ithaic.imitate_os.fileManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import lombok.Data;
import lombok.Getter;

@Data
public final class Disk {
    /*
     * TODO 磁盘初始化
     *  1. 磁盘分配
     *    0-3盘块为系统盘，存放FAT
     *    4盘块为根目录
     *    5-255盘块为数据盘，可存放目录块、文件数据
     *  2.FAT表内容含义
     *    0代表盘块空闲
     *    1代表盘块已被占用
     *    2 3 4 未分配
     *    5-255 代表文件/目录所在的盘块号
     *
     * */

    public static final int BLOCK_COUNT = 256; // 盘块总数
    public static final int BLOCK_SIZE = 64; // 每个盘块的大小
    @Getter
    private static final char[] readBuffer = new char[BLOCK_SIZE]; // 读缓冲区
    private static final char[] writeBuffer = new char[BLOCK_SIZE]; // 写缓冲区
    private static final String diskFileName = "src/main/resources/ithaic/imitate_os/disk"; // 磁盘文件名
    /**
     * 磁盘初始化函数*/
    public Disk() {
        int position = 0;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position);
            // 前4块盘区用于存放FAT
            for (int i = 0; i < 5; i++) {
                file.writeChar((char) 1);
            }
            // 后252块盘区自由使用
            for (int i = 5; i < BLOCK_SIZE * 4; i++) {
                file.writeChar((char) 0);
            }
            //初始化磁盘文件
            for (int i = BLOCK_SIZE * 4; i < BLOCK_SIZE * BLOCK_COUNT; i++) {
                file.writeChar((char) 0);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 此函数用于分配一个空闲的盘块号
     * @return 返回一个空闲的盘块号，如果没有空闲盘块，返回0
     */
    public static int getFreeBlock() {
        int position = 4;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "r");
            file.seek(position * 2);
            for (int i = 4; i < BLOCK_COUNT; i++) {
                if (file.readChar() == 0) {
                    return i;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


    /**
     * 写入整块64byte数据到磁盘
     * @param content 写入的字符串数组
     * @param blockNo 盘块号
     * */
    public static void writeBlock(char[] content, int blockNo) {
        int position = blockNo * BLOCK_SIZE;
        setWriteBuffer(content);
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position * 2L);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                file.writeChar(writeBuffer[i]);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 将目录块写入磁盘
     * @param content 目录块内容
     * @param blockNo 盘块号
     * @param length 写入缓冲区的长度
     * @return 返回写入的起始地址
     * */
    public static int  writeCatalogItem(char[] content, int blockNo, int length) {
        // 先读磁盘，看看此盘块是否满了，可否新建文件/文件夹
        int position = blockNo * BLOCK_SIZE;
        readBlock(blockNo);
        for (int i = 0; i < 8; i++) {
            if(readBuffer[i * 8]==0){
                position = blockNo * BLOCK_SIZE + i * 8;//得到起始位置
                break;
            }
            if(i == 7){
                System.out.println("磁盘已满");
                return 0;
            }
        }
        //设置目录块内容
        setWriteBuffer(content);
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position * 2L);
            for (int i = 0; i < length; i++) {
                file.writeChar(writeBuffer[i]);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return position;
    }


    /**
     * 单个字符指定位置写入磁盘
     * @param content 写入的字符
     * @param startBlock 起始盘块号
     * @param position 写入位置
     * */
    public static void writeChar(char content, int startBlock, int position) {
        char[] content1 = new char[1];
        content1[0] = content;
        setWriteBuffer(content1);
        int position1 = startBlock * BLOCK_SIZE + position;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position1 * 2L);
            file.writeChar(writeBuffer[0]);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 此函数用于修改目录块
     * @param content 要修改的内容
     * @param blockNo 盘块号
     * @param position 要修改的位置
     * */
     public static void modifyCatalogItem(char[] content, int blockNo, int position) {
        setWriteBuffer(content);
        int position1 = blockNo * BLOCK_SIZE + position;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position1 * 2L);
            for (int i = 0; i < 8; i++) {
                file.writeChar(writeBuffer[i]);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }


     /**
     * 此函数指定盘块号，读取磁盘内容到缓冲区中
     * @param blockNo 盘块号
     * */
    public static char[] readBlock(int blockNo) {
        int position = blockNo * BLOCK_SIZE;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "r");
            file.seek(position * 2L);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                readBuffer[i] = file.readChar();
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBuffer;
    }


    /**
     * 这个函数用于查找文件/文件夹所在的盘块号（创建文件/目录时使用）, 你必须保证你传入的路径全是目录
     * @param path 一个字符串数组，表示文件的路径(不包括文件本身）
     * @return 返回文件夹/文件所在的盘块号，如果不存在，返回0
     */
    public static int findBottomFileBlock(String[] path){
        char blockNo = 4; //根目录所在的盘块号
        for (int i = 0; i < path.length; i++) {
            readBlock(blockNo);
            for (int j = 0; j < 8; j++) {
                if(path[i].equals(new String(readBuffer, j * 8, 3).trim()) && readBuffer[j * 8 + 4] == 0x80){
                    blockNo = readBuffer[j * 8 + 5];
                    break;
                }
                if(j == 7){
                    System.out.println("文件不存在");
                    return 0;
                }
            }
        }
        return blockNo;
    }

    /**
     * 这个函数用于查找文件/文件夹所在的盘块号
     * @param path 一个字符串数组，表示文件的路径(不包括文件本身）
     * @param fileName 文件名
     * @param property 文件属性 0x80表示文件夹，0x40表示可执行文件，0x20表示普通文件
     * @return 返回文件夹/文件所在的盘块号，如果不存在，返回0
     */
    public static int findBottomFileBlock(String[] path, String fileName, int property){
        char blockNo = 4; //根目录所在的盘块号
        //前面path.length-1个盘块是必须是目录盘块，最后一个盘块是文件/文件夹所在的盘块
        for (int i = 0; i < path.length; i++) {
            readBlock(blockNo);
            for (int j = 0; j < 8; j++) {
                if(path[i].equals(new String(readBuffer, j * 8, 3).trim()) && readBuffer[j * 8 + 4] == 0x80){
                    blockNo = readBuffer[j * 8 + 5];
                    break;
                }
                if(j == 7){
                    System.out.println("文件不存在");
                    return 0;
                }
            }
        }
        //检查最后一个文件名与文件里类型是否匹配
        readBlock(blockNo);
        for (int i = 0; i < 8; i++) {
            if(fileName.equals(new String(readBuffer, i * 8, 3).trim()) && readBuffer[i * 8 + 4]  == property){
                blockNo = readBuffer[i * 8 + 5];
                break;
            }
            if(i == 7){
                System.out.println("文件不存在");
                return 0;
            }

        }
        return blockNo;
    }

    /**
     * 此函数用于设置写缓冲区内容
     * @param content 一个字符串数组
     * */
    private static void setWriteBuffer(char[] content){
        Arrays.fill(writeBuffer, (char) 0);//初始化写缓冲区
        for (int i = 0; i < content.length; i++) {
            writeBuffer[i] = content[i];
        }
    }
    /**
     * 设置FAT表项
     * @param content: 要写入的内容
     * @param where:   要写入的位置
     * */
    public static void setFAT(int content, int where){
        Disk.writeChar((char)content, (char)(where/64), (char)(where%64));//设置子目录的FAT表项
    }

    /**
     * 打印指定盘块内容，供调试使用
     * @param blockNo 盘块号*/
    public static void printBlock(int blockNo){
        char[] content = readBlock(blockNo);
        System.out.print("Block "+blockNo+": ");
        for (int i = 0; i < 64; i++) {
            if(content[i] == 0) System.out.print("0");
            else if (content[i] == 1) System.out.print("1");
            else if (content[i] == 2) System.out.print("2");
            else if (content[i] == 3) System.out.print("3");
            else if (content[i] == 4) System.out.print("4");
            else if (content[i] == 5) System.out.print("5");
            else if (content[i] == 6) System.out.print("6");
            else if (content[i] == 7) System.out.print("7");
            else if (content[i] == 8) System.out.print("8");
            else if (content[i] == 9) System.out.print("9");
            else if (content[i] == 0x20) System.out.print("F");
            else if (content[i] == 0x40) System.out.print("E");
            else if (content[i] == 0x80) System.out.print("D");
            else System.out.print(content[i]);
            if((i+1)%8==0)System.out.print("  ");
        }
        System.out.println();
    }

}



