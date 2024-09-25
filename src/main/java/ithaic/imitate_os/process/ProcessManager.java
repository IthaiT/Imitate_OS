package ithaic.imitate_os.process;

import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;
import lombok.Data;
import lombok.Getter;

import java.util.*;

import static ithaic.imitate_os.process.StateCode.BLOCKED;
import static ithaic.imitate_os.process.StateCode.READY;

@Data
public class ProcessManager {
    //进程阻塞队列
    private Queue<PCB> blockedProcessQueue;
    //进程就绪队列
    private Queue<PCB> readyProcessQueue;
    //进程空白队列
    private Queue<PCB> blankProcessQueue;
    //进程PID集合，存储所有存活进程的PID
    private Set<Integer> activePIDs;
    //下一个可用的PID
    private int nextPID;

    @Getter
    private static ProcessManager instance;

    static {
        instance = new ProcessManager();
    }

    {
        blockedProcessQueue = new LinkedList<>();
        readyProcessQueue = new LinkedList<>();
        blankProcessQueue = new LinkedList<>();
        activePIDs = new HashSet<>();
        nextPID = 1; //进程PID从1开始
    }

    public ProcessManager() {
        //初始化空白队列
        PCB[] pcb = Memory.getInstance().getPcbTable();
        blankProcessQueue.addAll(Arrays.asList(pcb).subList(0, 10));
    }

    /**
     * 保存当前进程状态
     */
    public void saveProcessState() {
        CPU cpu = CPU.getInstance();
        PCB runningProcess = cpu.getRunningProcess();
        if (runningProcess == null) return;

        runningProcess.setPC(cpu.getPC());
        runningProcess.setAX(cpu.getAX());
        runningProcess.setPSW(cpu.getPSW());
        runningProcess.setRunningTime(cpu.getRelativeClock());
        runningProcess.setState(READY);
    }

    /**
     * 恢复当前进程状态
     */
    public void restoreProcessState() {
        CPU cpu = CPU.getInstance();
        PCB runningProcess = cpu.getRunningProcess();
        if (runningProcess == null) return;

        cpu.setPC(runningProcess.getPC());
        cpu.setAX(runningProcess.getAX());
        cpu.setPSW(runningProcess.getPSW());
        cpu.setRelativeClock(runningProcess.getRunningTime() == 0 ? cpu.getTimeSlice() : runningProcess.getRunningTime());
    }

    /**
     * 创建进程，并加入就绪队列
     * @param execFile 传入可执行文件的内容
     * @return 创建成功返回true，失败返回false
     */
    public boolean create(char[] execFile) {
        PCB pcb = blankProcessQueue.poll();
        if (pcb == null) return false;

        MemoryManager memoryManager = MemoryManager.getInstance();
        MemoryBlock memoryBlock = memoryManager.allocate(execFile.length);

        if (memoryBlock == null) {
            blankProcessQueue.add(pcb);
            return false;
        }

        memoryManager.write(memoryBlock, execFile);
        pcb.setAllocatedMemory(memoryBlock);
        pcb.setPid(getAvailablePID());
        pcb.setState(READY);
        readyProcessQueue.add(pcb);
        return true;
    }

    /**
     * 撤销进程，并回收进程所占内存
     * @param pcb 进程控制块
     */
    public void destroy(PCB pcb) {
        if (pcb == null) return;

        MemoryManager.getInstance().release(pcb.getAllocatedMemory());
        activePIDs.remove(pcb.getPid());
        pcb.init();
        blankProcessQueue.add(pcb);
    }

    /**
     * 阻塞进程，并将进程保存到阻塞队列
     */
    public void block(PCB pcb) {
        if (pcb == null) return;

        saveProcessState();
        pcb.setState(BLOCKED);
        blockedProcessQueue.add(pcb);
    }

    /**
     * 唤醒进程，并将进程保存到就绪队列
     */
    public void awake(PCB pcb) {
        if (pcb == null) return;

        if (blockedProcessQueue.remove(pcb)) {
            pcb.setState(READY);
            readyProcessQueue.add(pcb);
        }
    }

    /**
     * 获取可用的PID
     * @return 可用的PID
     */
    private int getAvailablePID() {
        while (activePIDs.contains(nextPID)) {
            nextPID++;
        }
        activePIDs.add(nextPID);
        return nextPID;
    }
}
