package ithaic.imitate_os.process;


import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryBlock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PCB {
    private int pid;
    private String state;//进程状态
    private MemoryBlock allocatedMemory;
    private int PC;
    private char PSW;
    private int AX;
    private String blockedReason; //阻塞原因


    {
        PC = 0;
        PSW = 0b000;
        AX = 0;
    }

    public void clear() {
        pid = 0;
        PC = 0;
        PSW = 0b000;
        AX = 0;
        state = null;
        allocatedMemory = null;
        blockedReason = null;
    }
}
