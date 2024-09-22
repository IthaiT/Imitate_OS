package ithaic.imitate_os.process;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Thread.sleep;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPU {
    private  ProcessManager processManager;
    private  PCB runningProcess;
    private  String IR;
    private  int PC;
    private  char PSW;
    private  int AX;

    {
        processManager = new ProcessManager();
    }

    //模拟CPU运行
    public void run(){
        Runnable task = ()->{
            while(true){
                checkPSW();
                //取就绪进程
                runningProcess = processManager.getReadyProcessQueue().peek();

                //到内存取指令
                //fetchInstruction();

                //解析并执行指令
                parseInstruction();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
        };
        task.run();
    }

    //检查PSW 判断是否需要中断处理
    private void checkPSW(){
        if((PSW & 0b001) == 0b001){
            //TODO: 程序结束中断
        }
        if((PSW & 0b010) == 0b010){
            //TODO: 时间片结束中断
        }
        if ((PSW & 0b100) == 0b100){
            //TODO: I/O中断
        }
    }

    //解析指令
    private void parseInstruction( ){
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
            System.out.println("程序结束 释放内存");
        }
    }


}
