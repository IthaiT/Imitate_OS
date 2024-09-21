package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.fileKind.File;

public class Directory implements File {
    @Override
    public void create(String path) {
        int startNum = Disk.getFreeBlock();
        if(startNum == 0){
            System.out.println("No free block available");
            return;
        }
        if(path.length() > 3 || path.isEmpty()){
            System.out.println("Path too long");
            return;
        }
        if(path.contains("/") || path.contains(".") || path.substring(1).contains("$")){
            System.out.println("Invalid path");
            return;
        }
        Disk.setBlock(startNum, path.toCharArray(), (char) 0, (char) 0X80, (short) 1);
    }

    @Override
    public void delete(String path) {

    }
}
