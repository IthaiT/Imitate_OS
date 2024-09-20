package ithaic.imitate_os.fileManager;

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
        capacity[0][0] = 1;
        capacity[0][1] = 1;
    }


}
