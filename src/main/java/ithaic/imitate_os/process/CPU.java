package ithaic.imitate_os.process;
import ithaic.imitate_os.deviceManager.DeviceManager;
import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryManager;
import lombok.Data;
import lombok.Getter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ithaic.imitate_os.process.StateCode.RUNNING;


@Data
public class CPU {
    private final int timeSlice;  // 调度算法的时间片
    private int relativeClock;
    private int systemClock;
    private Memory memory;
    private ProcessManager processManager;
    private MemoryManager memoryManager;
    private PCB runningProcess;
    private String IR; //指令寄存器
    private int PC;   // 程序计数器
    private char PSW;  // 程序状态字
    private int AX;    // 累加器
    private Runnable task;
    @Getter
    private int systemClockLabel = 0;//系统时钟标签0
    @Getter
    private int relativeClockLabel = 0;//时间片标签0
    @Getter
    private String processStatus = "";//进程状态标签传参

    @Getter
    private String processResult = null;//进程结果标签传参
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
     * 模拟CPU 1秒运行一次
     */
    // 模拟CPU运行
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::runTask, 0, 1, TimeUnit.SECONDS);
    }


    //CPU运行流程
    private void runTask() {
        checkPSW();

        if (runningProcess == null) {
            processScheduling();
            if(runningProcess != null){
                executeRunningProcess();
            }
            //System.out.println("没有进程, CPU空转");
        } else {
            executeRunningProcess();
        }

        //System.out.println("系统时钟: " + systemClock);
        //立刻调用设置时间
        setLabelClock();


        systemClock++;
        if (relativeClock == 0) {
            PSW |= 0b010;  // 置时间片结束中断位为1
        }
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
        //System.out.println("进程 " + runningProcess.getPid() + " 运行中, 时间片剩余: " + relativeClock);
        //更新进程状态
        setLabelRelativeClock();
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
        runningProcess.setState(RUNNING);
    }

    /**
     * 检查程序状态字
     */
    private void checkPSW() {
        if ((PSW & 0b001) == 0b001) {
            processManager.destroy(runningProcess);
            runningProcess = null;
            IR = null;
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
        switch (IR) {
            case "end":
                PSW |= 0b001;  // 设置程序结束中断
                setProcessState("end");//进程过程界面显示end

                setProcessResult(String.valueOf(AX));//进程结果界面显示AX
                break;
            case "x++":
                setProcessState("AX++, AX = " + ++AX);//进程过程界面显示

                setProcessResult(null);//进程结果界面显示计算中
                break;
            case "x--":
                setProcessState("AX--, AX = " + --AX);//进程过程界面显示

                setProcessResult(null);//进程结果界面显示计算中
                break;
            default:
                if (IR.startsWith("x=")) {
                    AX = Integer.parseInt(IR.substring(2));
                    setProcessState("AX = " + AX);//进程过程界面显示
                } else if (IR.startsWith("!")) {
                    char deviceName = IR.charAt(1);
                    int requestTime = Integer.parseInt(IR.substring(2));
                    //TODO:处理I/O请求,申请设备资源,此处应该调用设备管理的相关函数
                    DeviceManager deviceManager = DeviceManager.getInstance();
                    deviceManager.allocateDevice(runningProcess, String.valueOf(deviceName), requestTime);
                    PSW |= 0b100;  // 设置I/O请求中断
                }
                break;
        }
    }

    //设置Label时间
    private void setLabelClock() {
        systemClockLabel = systemClock;
    }

    //返回Label时间
    public int getLabelClock() {
        return systemClockLabel;
    }

    private void setLabelRelativeClock() {
        relativeClockLabel = relativeClock;
    }

    public int getLabelRelativeClock() {
        return relativeClockLabel;
    }

    private void setProcessState(String str) {
        processStatus = str;
    }

    public String getProcessState() {
        return processStatus;
    }

    private void setProcessResult(String result) {
        processResult = result;
    }
}
