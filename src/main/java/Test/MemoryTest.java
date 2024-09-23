package Test;


import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;
import ithaic.imitate_os.process.CPU;
import ithaic.imitate_os.process.PCB;
import org.junit.Test;


//单元测试类
public class MemoryTest {

    @Test
    public void Test() {
        CPU cpu = new CPU();
        MemoryManager memoryManager = cpu.getMemoryManager();
        MemoryBlock block = memoryManager.allocate(100);
        PCB pcb = new PCB(1,"running",block,0, (char) 0,0,"null");
        cpu.setRunningProcess(pcb);

        String executableFile = "X=1000;X--;\0\0\0X++    !A10 X-- X=9999 X-- X--  end";

        memoryManager.write(pcb.getAllocatedMemory(),executableFile.toCharArray());

        cpu.run();
    }
}
