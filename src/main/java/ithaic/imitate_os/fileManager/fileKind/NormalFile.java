package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.Disk;
import ithaic.imitate_os.fileManager.fileKind.File;

public class NormalFile implements File {

    @Override
    public void create(String path) {
        int startNum = Disk.getFreeBlock();
        if(startNum == 0){
            System.out.println("No free block available");
            return;
        }
        if(path.contains("/") || path.contains(".") || path.contains("$")){
            System.out.println("Invalid path");
            return;
        }
        if(path.length() > 3 || path.isEmpty()){
            System.out.println("Path too long");
            return;
        }
        Disk.setBlock(startNum, path.toCharArray(), (char)0, (char) 0X20, (short) 1);
    }



    @Override
    public void delete(String path) {

    }
}
