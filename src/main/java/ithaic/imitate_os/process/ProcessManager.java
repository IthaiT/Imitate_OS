package ithaic.imitate_os.process;

import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;
import lombok.Data;
import lombok.Getter;

import java.util.*;

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

    static{
        instance = new ProcessManager();
    }

    {
        blockedProcessQueue = new LinkedList<>();
        readyProcessQueue = new LinkedList<>();
        blankProcessQueue = new LinkedList<>();
        activePIDs = new HashSet<>();
        nextPID = 1;//进程PID从1开始
    }

    public ProcessManager(){
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
        if(runningProcess == null)return;
        runningProcess.setPC(cpu.getPC());
        runningProcess.setAX(cpu.getAX());
        runningProcess.setPSW(cpu.getPSW());
        runningProcess.setRunningTime(cpu.getRelativeClock());
        runningProcess.setState("Ready");
    }


    /**
     * 恢复当前进程状态
     */
    public void restoreProcessState() {
        CPU cpu = CPU.getInstance();
        PCB runningProcess = cpu.getRunningProcess();
        if(runningProcess == null)return;
        cpu.setPC(runningProcess.getPC());
        cpu.setAX(runningProcess.getAX());
        cpu.setPSW(runningProcess.getPSW());
        cpu.setRelativeClock(runningProcess.getRunningTime() == 0 ? cpu.getTimeSlice() : runningProcess.getRunningTime());
    }


    /**
     * 该原语创建进程,并加入就绪队列
     * @param execFile 传入可执行文件的内容
     * @return 创建成功返回true，失败返回false
     */
    //进程创建
    public boolean create(char[] execFile){
        PCB pcb = this.blankProcessQueue.poll();
        if(pcb == null)return false;
        //分配内存,并将可执行文件内容载入内存
        MemoryManager memoryManager =MemoryManager.getInstance();
        MemoryBlock memoryBlock= memoryManager.allocate(execFile.length);
        //内存分配失败
        if(memoryBlock == null){
            this.blankProcessQueue.add(pcb);
            return false;
        }
        memoryManager.write(memoryBlock,execFile);
        //设置PCB所占据的内存块
        pcb.setAllocatedMemory(memoryBlock);
        //找到可用的PID
        while(activePIDs.contains(nextPID)){
            nextPID++;
        }
        pcb.setPid(nextPID);
        activePIDs.add(nextPID);
        //加入就绪队列
        pcb.setState("Ready");
        this.readyProcessQueue.add(pcb);
        return true;
    }


    /**
     * 该原语撤销进程,并回收进程所占内存
     * @param pcb 进程控制块
     */
    //进程撤销
    public void destroy(PCB pcb){
        if(pcb == null)return;
        //回收进程所占内存
        MemoryBlock memoryBlock = pcb.getAllocatedMemory();
        MemoryManager.getInstance().release(memoryBlock);
        //回收进程PCB
        activePIDs.remove(pcb.getPid());
        pcb.init();
        this.blankProcessQueue.add(pcb);
    }

    /**
     * 该原语阻塞进程,并将进程保存到阻塞队列
     */
    //进程阻塞
    public void block(PCB pcb){
        if(pcb == null)return;
        saveProcessState();
        pcb.setState("Blocked");
        this.blockedProcessQueue.add(pcb);
    }


    /**
     * 该原语唤醒进程,并将进程保存到就绪队列
     */
    //进程唤醒
    public void awake(PCB pcb){
        if(pcb == null)return;
        boolean hasProcess = this.blockedProcessQueue.remove(pcb);
        if(hasProcess){
            pcb.setState("Ready");
            this.readyProcessQueue.add(pcb);
        }
    }

}
