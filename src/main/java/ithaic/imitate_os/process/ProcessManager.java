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
        //加入就绪队列
        pcb.setState("Ready");
        this.readyProcessQueue.add(pcb);
        return true;
    }

    //进程撤销
    public void destroy(PCB pcb){
        //回收进程所占内存
        MemoryBlock memoryBlock = pcb.getAllocatedMemory();
        MemoryManager.getInstance().release(memoryBlock);
        //回收进程PCB
        pcb.clear();
        this.blankProcessQueue.add(pcb);
    }
    //TODO: 进程阻塞
    public  void block(){

    }
    //TODO: 进程唤醒
    public  void awake(){

    }

}
