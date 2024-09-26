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
        CPU cpu = CPU.getInstance();


        Thread t = new Thread(()->{
            ProcessManager processManager = ProcessManager.getInstance();

            String executableFile = "X=1000;X--;\0\0\0X++     X-- X=9999 X-- X--  x-- x-- x-- end";
            String executableFile1 = "X=1;X++;\0\0\0X++     X++ X=520 X-- X--  end";
            String executableFile2 = "X=100 x=101 x=102 x=103 x=104 x=105 x=106 x=107 x=108 x=109 x=110 x=111 x=112 x=113 x=114 end";

            processManager.create(executableFile.toCharArray());
            processManager.create(executableFile1.toCharArray());
            processManager.create(executableFile2.toCharArray());
        });
        t.start();
        cpu.run();
        while(true){}
    }
}
