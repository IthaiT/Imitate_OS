package Test;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.FileInteract;
import ithaic.imitate_os.fileManager.FileUtils;
import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import org.junit.Test;

public class fileManagerTest {
    @Test
    public void test() {
        String command = "sdajhsdjkahsk /abc";
        new Disk();
        String[] commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        MyFile file = new MyFile(directoryArray);
        file.writeData("hello world".toCharArray());
        file.writeData("hello world2".toCharArray());

        FileUtils.typeFile(directoryArray);

        FileUtils.deleteFile(directoryArray);
        System.out.println("delete");
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
            if(buffer[i] == 2)buffer[i] = '2';
        }
        System.out.println(buffer);
        Disk.readBlock(5);
        buffer = Disk.getReadBuffer();
        for (int i = 0; i < 64; i++) {
            if(buffer[i] == 0)buffer[i] = '0';
            if(buffer[i] == 1)buffer[i] = '1';
        }
        System.out.println(buffer);
        Disk.readBlock(6);
        buffer = Disk.getReadBuffer();
        for (int i = 0; i < 64; i++) {
            if(buffer[i] == 0)buffer[i] = '0';
            if(buffer[i] == 1)buffer[i] = '1';
        }
        System.out.println(buffer);
    }
}
