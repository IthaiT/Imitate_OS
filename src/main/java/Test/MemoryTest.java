package Test;

import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;
import org.junit.Test;
import ithaic.imitate_os.process.CPU;

//单元测试类
public class MemoryTest {

    @Test
    public void memoryTest() {
        Memory memory = new Memory();
        MemoryManager memoryManager = new MemoryManager(memory);

        memoryManager.allocate(100);
        MemoryBlock block1 = memoryManager.allocate(200);
        MemoryBlock block2 = memoryManager.allocate(100);
        MemoryBlock block3 = memoryManager.allocate(112);

        memoryManager.release(block1);

        memoryManager.release(block3);

        memoryManager.release(block2);
    }
}
