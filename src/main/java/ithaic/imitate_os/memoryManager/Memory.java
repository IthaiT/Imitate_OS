package ithaic.imitate_os.memoryManager;

import ithaic.imitate_os.process.PCB;
import lombok.Data;
import lombok.Getter;

/**
 * 内存
 */
@Data
public class Memory {
    private PCB[] pcbTable;
    private MemoryBlock memoryBlock; //内存分配表表头
    private char[] UserMemory;

    @Getter
    private static Memory instance;

    static {
        instance = new Memory();
    }

    //初始化内存
    {
        //最多容纳10个进程
        pcbTable = new PCB[10];
        //初始化PCB数组
        for (int i = 0; i < 10; i++){
            pcbTable[i] = new PCB();
        }
        //初始状态内存块大小为512,且为空闲状态
        memoryBlock = new MemoryBlock(0, MemoryManager.MEMORY_SIZE,true,null,null);
        //初始化用户内存
        UserMemory = new char[512];
    }


    @Override
    public String toString() {
        return "Memory";
    }
}
