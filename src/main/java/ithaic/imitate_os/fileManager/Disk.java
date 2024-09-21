package ithaic.imitate_os.fileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.Test;


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
    private static final char[] readBuffer = new char[BLOCK_SIZE]; // 读缓冲区
    private static final char[] writeBuffer = new char[BLOCK_SIZE]; // 写缓冲区
    private static final String diskFileName = "src/main/resources/ithaic/imitate_os/disk"; // 磁盘文件名

    //TODO 磁盘初始化
    Disk() {
        int position = 0;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position);
            // 前4块盘区用于存放FAT，第5块盘区用于存放根目录
            for (int i = 0; i < 5; i++) {
                file.writeChar(1);
            }
            // 后251块盘区自由使用
            for (int i = 5; i < BLOCK_SIZE * 4; i++) {
                file.writeChar(0);
            }
            //初始化磁盘文件
            for (int i = BLOCK_SIZE * 4; i < BLOCK_SIZE * BLOCK_COUNT; i++) {
                file.writeChar(0);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO 得到空闲盘块号
    public static int getFreeBlock() {
        int position = 0;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "r");
            file.seek(position);
            for (int i = 5; i < BLOCK_SIZE; i++) {
                if (file.readChar() == 0) {
                    return i;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    //TODO 写入磁盘
    public static void writeBlock(int blockNo, char[] data) {
        int position = blockNo * BLOCK_SIZE;
        try {
            RandomAccessFile file = new RandomAccessFile(diskFileName, "rw");
            file.seek(position);
            for (char datum : data) {
                file.writeChar(datum);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}



