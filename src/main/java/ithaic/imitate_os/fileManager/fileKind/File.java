package ithaic.imitate_os.fileManager.fileKind;

public interface File {
    /*TODO
    *   1. 文件属性
    *       1.1 0X80 目录
    *       1.2 0X40 可执行文件
    *       1.3 0X20 普通文件
    * */
    char[] name = new char[3];// 文件名
    char extendedName = 0;// 扩展名
    char property = 0;// 文件属性
    char startBlock = 0;// 起始盘块号
    char length = 0;// 文件大小，单位为盘块数
    /**
    * @param path: 用户创建的文件路径
     *
    * */
   public void create(String path);
    /**
     * @param name: 文件名
     * @param extendedName: 扩展名
     * @param property: 文件属性
     * @param startBlock: 起始盘块号
     * @param length: 文件大小，单位为盘块数
     *
    * */
    public void create(char[] name, char extendedName, char property, char startBlock, char length);
    /**
    * @param path: 用户要删除的文件的绝对路径
     *
    * */
    void delete(String path);
}
