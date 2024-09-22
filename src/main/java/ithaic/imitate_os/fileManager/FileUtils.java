package ithaic.imitate_os.fileManager;

public class FileUtils {
    /**
     * 删除文件
     * @param filePath 文件路径数组
     * */
    public static void deleteFile(String[] filePath) {
        //判断文件是否存在
        if(!isFileExists(filePath)){
            System.out.println("File not found");
            return;
        }
        //得到目录块的位置
        int position = getCatalogItemPosition(filePath);
        char[] block = Disk.readBlock(position/64);
        //将文件占据的盘块清除,并将对应的FAT设置为0
        int ptr = block[position%64 + 5];//得到第一页的指针
        while(ptr != 1){
            Disk.writeBlock(new char[64], ptr); //清除文件占据的盘块
            char[] temp = Disk.readBlock(ptr/64);//获得FAT表
            int tempPtr = ptr; //暂存指针
            Disk.setFAT(0,ptr);//设置FAT表
            ptr = temp[tempPtr%64]; //获得下一页的指针
        }
        //将目录块清除
        for (int i = 0; i < 8; i++) {
            Disk.writeChar((char)0,position/64, position%64 + i );
        }
        if(position%64 == 0 && position/64 != 4){
            Disk.setFAT(0,position);
        }
    }
    public static void writeToFile(String filePath, String content) {

    }
    public static String readFile(String filePath) {
        return "" ;
    }
    public static void typeFile(String[] filePath) {

    }
    public static void isDirectory(String[] filePath) {

    }

    /**
     * 判断文件/文件夹是否存在
     * @param mixedArray 文件路径数组
     * @return true:存在 false:不存在*/
    public static boolean isFileExists(String[] mixedArray) {
        String filename = mixedArray[mixedArray.length-1];
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        // i从1开始，因为按照/分割，第一个是空字符串
        String[] directoryArray = new String[mixedArray.length-1];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        directoryArray[directoryArray.length-1] = filename;
        if(Disk.findBottomFileBlock(directoryArray)==0)
            return false;
        return true;
    }


    /**
     * 获得目录项在磁盘中的位置
     * @param mixedArray 文件路径数组
     * @return 目录项在磁盘中的位置
     * */
    private static int getCatalogItemPosition(String[] mixedArray) {
        String filename = mixedArray[mixedArray.length-1];
        if(filename.endsWith(".e")){
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        String[] directoryArray = new String[mixedArray.length-2];
        for (int i = 1; i < mixedArray.length - 1; i++) {
            directoryArray[i-1] = mixedArray[i];
        }
        int position = Disk.findBottomFileBlock(directoryArray);
        char[] block = Disk.readBlock(position);
        for (int i = 0; i < block.length; i += 8) {
            String catalogItem = new String(block, i*8, 3).trim();
            if(catalogItem.equals(filename)){
                return position* Disk.BLOCK_SIZE + i;
            }
        }
        return 0;
    }
}
