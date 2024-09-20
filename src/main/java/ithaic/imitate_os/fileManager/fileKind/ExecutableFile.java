package ithaic.imitate_os.fileManager.fileKind;

import ithaic.imitate_os.fileManager.fileKind.File;

public class ExecutableFile implements File {
    @Override
    public void create(String path) {

    }

    @Override
    public void create(char[] name, char extendName, char property, int startNum, int length) {

    }

    @Override
    public void delete(String path) {

    }
    /*
    * TODO 指令采用两个字节
    *  1、赋值指令：OX01 0X?
    *       ?为0-255任一数字
    *  2、加1指令：OX02 0X00
    *  3、减1指令：OX03 0X00
    *  4、特殊指令：0X21 0x?
    *       0x? 0x21代表! 第一个?代表A、B、C 第二个?代表0-255任一数字（时间）
    *  5、end指令：0X05 0X00
    * */

}
