package Test;

import org.junit.Test;
import ithaic.imitate_os.process.CPU;

//单元测试类
public class TestApplication {

    @Test
    public void cpuTest() {
        CPU cpu = new CPU();
        cpu.run();
    }
}
