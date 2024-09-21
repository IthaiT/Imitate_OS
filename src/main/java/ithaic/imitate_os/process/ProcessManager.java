package ithaic.imitate_os.process;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessManager {
    //进程阻塞队列
    private static Queue<Process> blockedProcessQueue;
    //进程就绪队列
    private static Queue<Process> readyProcessQueue;

    static {
        blockedProcessQueue = new LinkedList<>();
        readyProcessQueue = new LinkedList<>();
    }

    //TODO: 进程创建
    public static void create(){

    }
    //TODO: 进程撤销
    public static void destroy(){

    }
    //TODO: 进程阻塞
    public static void block(){

    }
    //TODO: 进程唤醒
    public static void awake(){

    }

}
