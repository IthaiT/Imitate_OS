package ithaic.imitate_os.process;


import ithaic.imitate_os.memoryManager.Memory;
import ithaic.imitate_os.memoryManager.MemoryBlock;
import ithaic.imitate_os.memoryManager.MemoryManager;

import lombok.Data;
import static java.lang.Thread.sleep;


@Data
public class CPU {
    private Memory memory;
    private ProcessManager processManager;
    private MemoryManager memoryManager;
    private PCB runningProcess;
    private String IR;
    private int PC;
    private char PSW;
    private int AX;

    {
        memory = Memory.getInstance();
        memoryManager = MemoryManager.getInstance();
        processManager = ProcessManager.getInstance();
    }

    //模拟CPU运行
    public void run(){
        Runnable task = ()->{
            while(true){
                checkPSW();
                //进程调度
                boolean hasProcess = processScheduling();
                if(!hasProcess){
                    System.out.println("没有进程,CPU空转");
                }else {
                    //到内存取指令
                    String instruction = memoryManager.fetchInstruction(runningProcess.getAllocatedMemory());
                    String[] instructions = instruction.split("[\\n;\\s\0]+");
                    try {
                        IR = instructions[PC];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //指令超出范围,程序结束
                        PSW = (char) (PSW | 0b001);
                        continue;
                    }
                    //解析并执行指令
                    parseInstruction();
                    PC++;
                    //延时1s,模拟时间片轮转
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        task.run();
    }

    //进程调度 时间片轮转法
    private boolean processScheduling(){
        if(runningProcess != null) {
            //保存当前进程状态到PCB
            runningProcess.setPC(PC);
            runningProcess.setAX(AX);
            runningProcess.setPSW(PSW);
            runningProcess.setState("Ready");
            //将当前进程添加到就绪队列
            processManager.getReadyProcessQueue().add(runningProcess);
        }
        //获取下一个进程
        runningProcess = processManager.getReadyProcessQueue().poll();
        if(runningProcess == null)return false;
        //恢复进程状态
        PC = runningProcess.getPC();
        AX = runningProcess.getAX();
        PSW = runningProcess.getPSW();
        runningProcess.setState("Running");
        return true;
    }

    //检查PSW 判断是否需要中断处理
    private void checkPSW(){
        if((PSW & 0b001) == 0b001){
            System.out.println("程序结束中断");
            //进程撤销原语
            processManager.destroy(runningProcess);
            runningProcess = null;
            PSW = (char) (PSW & 0b110);
        }
        if((PSW & 0b010) == 0b010){
            //TODO: 时间片结束中断
            System.out.println("时间片结束中断");
            PSW = (char)(PSW & 0b101);
        }
        if ((PSW & 0b100) == 0b100){
            //TODO: I/O中断
            System.out.println("I/O中断");
            PSW = (char)(PSW & 0b011);
        }
    }

    //解析指令
    private void parseInstruction(){
        IR = IR.toLowerCase();
        //TODO: 解析指令
        if(IR.startsWith("x=")){
            AX = Integer.parseInt(IR.substring(2));
            System.out.println("AX = " + AX);
        }
        if(IR.compareTo("x++")==0){
            AX++;
            System.out.println("AX++,AX = " + AX);
        }
        if(IR.compareTo("x--")==0){
            AX--;
            System.out.println("AX--,AX = " + AX);
        }
        if(IR.startsWith("!")){
            char deviceName =IR.charAt(1);
            int requestTime = Integer.parseInt(IR.substring(2));

            System.out.println("设备"+deviceName+" requestTime = " + requestTime);
        }
        if(IR.compareTo("end")==0){
            PSW = (char) (PSW | 0b001);
        }
    }


}
