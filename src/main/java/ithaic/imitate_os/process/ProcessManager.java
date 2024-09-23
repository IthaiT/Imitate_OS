package ithaic.imitate_os.process;

import lombok.Data;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

@Data
public class ProcessManager {
    //进程阻塞队列
    private Queue<PCB> blockedProcessQueue;
    //进程就绪队列
    private Queue<PCB> readyProcessQueue;
    //进程空白队列
    private Queue<PCB> blankProcessQueue;

    @Getter
    private static ProcessManager instance;

    static{
        instance = new ProcessManager();
    }

    {
        blockedProcessQueue = new LinkedList<>();
        readyProcessQueue = new LinkedList<>();
        blankProcessQueue = new LinkedList<>();
    }

    //TODO: 进程创建
    public void create(){
        PCB pcb = this.blockedProcessQueue.poll();
        if(pcb == null)return;

    }
    //TODO: 进程撤销
    public  void destroy(){

    }
    //TODO: 进程阻塞
    public  void block(){

    }
    //TODO: 进程唤醒
    public  void awake(){

    }
}
