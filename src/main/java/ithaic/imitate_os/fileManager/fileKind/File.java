package ithaic.imitate_os.fileManager.fileKind;

public interface File {
    /*TODO
    *   1. 文件属性
    *       1.1 0X80 目录
    *       1.2 0X40 可执行文件
    *       1.3 0X20 普通文件
    * */
    /**
    * @param path: 用户创建的文件路径
    * */
   public void create(String path);

    /**
    * @param path: 用户要删除的文件的绝对路径
    * */
    void delete(String path);
}
