package Test;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.FileUtils;
import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import org.junit.Test;

public class fileManagerTest {

    @Test
    public void test() {
        new Disk();
        String command = "sdajhsdjkahsk /abc";
        String[] commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        Directory file = new Directory(directoryArray);

        command = "sdasasa /abc/fil";
        String[] commandArray1 = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray1 = commandArray1[1].split("/"); // 以/分割目录数组
        MyFile file1 = new MyFile(directoryArray1);
        file1.writeData("hello world".toCharArray());

        String[] directoryArrayq = {"", "a","b","c"};
        FileUtils.hardCopyFile(directoryArray1, directoryArrayq);


        for (int i = 0; i < 10; i++) {
            Disk.printBlock(i);
        }
        String[] array2 = {"", "a"};
        FileUtils.deleteAllFiles(array2);
        for (int i = 0; i < 10; i++) {
            Disk.printBlock(i);
        }
    }

}

