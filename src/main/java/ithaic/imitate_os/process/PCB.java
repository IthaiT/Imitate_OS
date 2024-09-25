package ithaic.imitate_os.process;


import ithaic.imitate_os.memoryManager.MemoryBlock;

import lombok.Data;


@Data
public class PCB {
    private int pid;
    private int state;//进程状态
    private MemoryBlock allocatedMemory;
    private int PC;
    private char PSW;
    private int AX;
    private String blockedReason; //阻塞原因
    private int runningTime; //时间片


    {
        this.init();
    }

    public void init() {
        pid = 0;
        PC = 0;
        PSW = 0b000;
        AX = 0;
        state = -1;
        allocatedMemory = null;
        blockedReason = null;
        runningTime = 0;
    }
}
