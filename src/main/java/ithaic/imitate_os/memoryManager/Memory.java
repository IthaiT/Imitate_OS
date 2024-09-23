package ithaic.imitate_os.memoryManager;

import ithaic.imitate_os.process.PCB;
import lombok.Data;
import lombok.Getter;

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
        //初始状态内存块大小为512,且为空闲状态
        memoryBlock = new MemoryBlock(0,512,true,null,null);
        //初始化用户内存
        UserMemory = new char[512];
    }


    @Override
    public String toString() {
        return "Memory";
    }
}
