package Test;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.FileInteract;
import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import org.junit.Test;

import java.util.Arrays;
import java.util.Scanner;

public class fileManagerTest {
    @Test
    public void test() {
        String command = "sdajhsdjkahsk /abc";

        String[] commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        Disk disk = new Disk();
        Directory directory = new Directory();
        directory.create(directoryArray); // 创建目录
        command = "sdasd /abc/edf.e";
        commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        MyFile file = new MyFile();
        file.create(directoryArray); // 创建文件

        command = "sdasd /abc/edf.e";
        commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        file = new MyFile();
        file.create(directoryArray); // 创建文件

        Disk.readBlock(0);
        char[] buffer = Disk.getReadBuffer();
        for (int i = 0; i < 64; i++) {
            if(buffer[i] == 0)buffer[i] = '0';
            if(buffer[i] == 1)buffer[i] = '1';
        }
        System.out.println(buffer);

        Disk.readBlock(4);
        buffer = Disk.getReadBuffer();
        for (int i = 0; i < 64; i++) {
            if(buffer[i] == 0)buffer[i] = '0';
            if(buffer[i] == 1)buffer[i] = '1';
        }
        System.out.println(buffer);
        Disk.readBlock(5);
        buffer = Disk.getReadBuffer();
        for (int i = 0; i < 64; i++) {
            if(buffer[i] == 0)buffer[i] = '0';
            if(buffer[i] == 1)buffer[i] = '1';
        }
        System.out.println(buffer);

        System.out.println(Disk.getFreeBlock());

    }
}
