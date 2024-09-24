package ithaic.imitate_os.process;

import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryManager;
import lombok.Data;
import lombok.Getter;

import static java.lang.Thread.sleep;

@Data
public class CPU {
    private final int timeSlice;  // 调度算法的时间片
    private int relativeClock;
    private int systemClock;
    private Memory memory;
    private ProcessManager processManager;
    private MemoryManager memoryManager;
    private PCB runningProcess;
    private String IR;
    private int PC;
    private char PSW;  // 程序状态字
    private int AX;    // 累加器

    @Getter
    private static CPU instance;

    static {
        instance = new CPU(6);
    }

    {
        memory = Memory.getInstance();
        memoryManager = MemoryManager.getInstance();
        processManager = ProcessManager.getInstance();
    }

    public CPU(int timeSlice) {
        this.timeSlice = timeSlice;
    }

    /**
     * 建立新线程模拟CPU运行
     */
    // 模拟CPU运行
    public void run() {
        Runnable task = () -> {
            while (true) {
                checkPSW();

                if (runningProcess == null) {
                    processScheduling();
                    System.out.println("没有进程, CPU空转");
                } else {
                    executeRunningProcess();
                }

                System.out.println("系统时钟: " + systemClock);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                systemClock++;
                if (relativeClock == 0) {
                    PSW |= 0b010;  // 置时间片结束中断位为1
                }

            }
        };
        task.run();
    }

    /**
     * 执行当前运行进程
     */
    private void executeRunningProcess() {
        String instruction = memoryManager.fetchInstruction(runningProcess.getAllocatedMemory());
        String[] instructions = instruction.split("[\\n;\\s\0]+");

        try {
            IR = instructions[PC++];
        } catch (ArrayIndexOutOfBoundsException e) {
            PSW |= 0b001;  // 指令超出范围, 程序结束
            return;
        }

        parseInstruction();
        relativeClock--;
        System.out.println("进程 " + runningProcess.getPid() + " 运行中, 时间片剩余: " + relativeClock);
    }

    /**
     * 进程调度
     */
    private void processScheduling() {
        if (runningProcess != null) {
            processManager.saveProcessState();
            processManager.getReadyProcessQueue().add(runningProcess);
        }

        runningProcess = processManager.getReadyProcessQueue().poll();
        if (runningProcess == null) return;

        processManager.restoreProcessState();
        runningProcess.setState("Running");

        System.out.println("进程 " + runningProcess.getPid() + " 运行中, 时间片剩余: " + relativeClock);
    }


    /**
     * 检查程序状态字
     */
    private void checkPSW() {
        if ((PSW & 0b001) == 0b001) {
            processManager.destroy(runningProcess);
            runningProcess = null;
            PSW &= 0b110;  // 清除程序结束中断标志
        }

        if ((PSW & 0b010) == 0b010) {
            PSW &= 0b101;  // 清除时间片结束中断标志
            processScheduling();
        }

        if ((PSW & 0b100) == 0b100) {
            System.out.println("I/O中断");
            //阻塞当前进程, 并将其放入阻塞队列
            PSW &= 0b011;  // 清除I/O中断标志
            processManager.block(runningProcess);
            runningProcess = null;
        }
    }

    /**
     * 解析指令
     */
    private void parseInstruction() {
        IR = IR.toLowerCase();

        if (IR.startsWith("x=")) {
            AX = Integer.parseInt(IR.substring(2));
            System.out.println("AX = " + AX);
        } else if (IR.equals("x++")) {
            AX++;
            System.out.println("AX++, AX = " + AX);
        } else if (IR.equals("x--")) {
            AX--;
            System.out.println("AX--, AX = " + AX);
        } else if (IR.startsWith("!")) {
            char deviceName = IR.charAt(1);
            int requestTime = Integer.parseInt(IR.substring(2));
            //TODO:处理I/O请求

            PSW |= 0b100;  // 设置I/O请求中断
        } else if (IR.equals("end")) {
            PSW |= 0b001;  // 设置程序结束中断
        }
    }
}
