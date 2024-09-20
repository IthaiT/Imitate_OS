package ithaic.imitate_os.fileManager.fileKind;

public interface File {
    char[] name = new char[3];
    char extendName = 0;
    char property = 0;
    int startNum = 0;
    int length = 0;
    /**
    * @param path: 用户创建的文件路径
    * */
    void create(String path);
    /**
    * @param name: 文件名
    * @param extendName: 文件扩展名
    * @param property: 文件属性
    * @param startNum: 文件的起始盘号
    * @param length: 文件的长度
    * 这是上面create函数的内部调用函数
    * */
    void create(char[] name, char extendName, char property, int startNum, int length);
    /**
    * @param path: 用户要删除的文件的绝对路径
    * */
    void delete(String path);
}
