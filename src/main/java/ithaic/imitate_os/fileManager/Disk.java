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
    private static final int BLOCK_COUNT = 256; // 盘块总数
    private static final int BLOCK_SIZE = 64; // 每个盘块的大小
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
     * 将目录块写入磁盘
     * @param content 目录块内容
     * @param blockNo 盘块号
     * @param length 写入缓冲区的长度
     * */
    public static void writeCatalogItem(char[] content, int blockNo, int length) {
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
                return;
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
     * 此函数指定盘块号，读取磁盘内容到缓冲区中
     * @param blockNo 盘块号
     * */
    public static void readBlock(int blockNo) {
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
    }

    /**
     * 这个函数用于查找文件/文件夹所在的盘块号
     * @param path 一个字符串数组，表示文件的路径
     * @return 返回文件夹/文件所在的盘块号，如果不存在，返回0
     */
    public static int findBottomFileBlock(String[] path){
        char blockNo = 4; //根目录所在的盘块号
        for (int i = 0; i < path.length; i++) {
            readBlock(blockNo);
            for (int j = 0; j < 8; j++) {
                if(path[i].equals(new String(readBuffer, j * 8, 3).trim())){
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
     * 此函数用于设置写缓冲区内容
     * @param content 一个字符串数组
     * */
    private static void setWriteBuffer(char[] content){
        Arrays.fill(writeBuffer, (char) 0);//初始化写缓冲区
        for (int i = 0; i < content.length; i++) {
            writeBuffer[i] = content[i];
        }
    }

}



