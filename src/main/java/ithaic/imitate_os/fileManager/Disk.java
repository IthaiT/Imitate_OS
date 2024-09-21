package ithaic.imitate_os.fileManager;

import org.junit.Test;

public final class Disk {
    private static final char[][] capacity = new char[256][64];

    /*
    * TODO 磁盘初始化
    *  0 代表盘块空闲，1 代表已分配
    *  前两块盘块用来存放文件分配表FAT
    * */
    Disk() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 64; j++) {
                capacity[i][j] = 0;
            }
        }
        // 初始化根目录
        capacity[2][0] = '/';
        capacity[2][3] = 0;
        capacity[2][4] = 0x80;
        capacity[2][5] = 2;
        capacity[2][7] = 1;


        capacity[0][0] = 1;//第0个盘块已被占用，存放FAT
        capacity[0][1] = 1;//第1个盘块已被占用，存放FAT
    }


    public static int getFreeBlock() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 64; j++) {
                if (capacity[i][j] == 0) {
                    return i * 64 + j;
                }
            }
        }
        return 0;
    }
    public static void setBlock(int block, char[] name, char extendName, char property, short length) {
        //TODO 父目录的FAT需要更新
        capacity[block / 64][block % 64] = 1;
        capacity[block][0] = name[0];
        capacity[block][1] = name[1];
        capacity[block][2] = name[2];
        capacity[block][3] = extendName;
        capacity[block][4] = property;
        capacity[block][5] = (char) (length >> 8);
        capacity[block][6] = (char) (length & 0xff);
    }


}
