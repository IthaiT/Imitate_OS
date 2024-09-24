package Test;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.FileUtils;
import ithaic.imitate_os.fileManager.fileKind.Directory;
import ithaic.imitate_os.fileManager.fileKind.MyFile;
import org.junit.Test;

import java.util.ArrayList;

public class fileManagerTest {

    @Test
    public void test() {
        new Disk();

        String command = "create /abc";
        String[] commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        String[] directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        ArrayList<String> currentDirectory = new ArrayList<>();

        Directory file = new Directory(directoryArray);

        currentDirectory.add("");
        currentDirectory.add("abc");
        command = "create ..";
        commandArray = command.trim().split("\\s+"); // 去除空格，并以空格分割命令数组
        directoryArray = commandArray[1].split("/"); // 以/分割目录数组
        for (String s : directoryArray) {
            if (s.isEmpty()) continue;
            currentDirectory.add(s);
        }
        directoryArray = new String[currentDirectory.size()];
        for(int i=0;i<currentDirectory.size();i++){
            directoryArray[i] = currentDirectory.get(i);
        }

        for (int i = 0; i < 10; i++) {
            Disk.printBlock(i);
        }

    }

}

