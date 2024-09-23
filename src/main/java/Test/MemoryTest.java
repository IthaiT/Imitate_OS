package Test;


import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;
import ithaic.imitate_os.process.CPU;

import ithaic.imitate_os.process.ProcessManager;
import org.junit.Test;


//单元测试类
public class MemoryTest {

    @Test
    public void Test() throws InterruptedException {
        CPU cpu = new CPU();

        Thread thread = new Thread(()->cpu.run());
        Thread thread1 = new Thread(()-> {
            ProcessManager processManager = ProcessManager.getInstance();

            String executableFile = "X=1000;X--;\0\0\0X++    !A10 X-- X=9999 X-- X--  end";
            String executableFile1 = "X=1;X++;\0\0\0X++    !B9 X++ X=520 X-- X-- .................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................. end";

            processManager.create(executableFile.toCharArray());
            processManager.create(executableFile1.toCharArray());
        });
        thread.start();
        thread1.start();


        thread1.join();
        thread.join();

    }
}
