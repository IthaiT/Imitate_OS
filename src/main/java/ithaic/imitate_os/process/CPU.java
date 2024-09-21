package ithaic.imitate_os.process;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPU {
    private  PCB runningProcess;
    private  String IR;
    private  int PC;
    private  char PSW;
    private  int AX;

    //模拟CPU运行
    public void run(){
//        while(true){
            checkPSW();
            //TODO: 解析指令，执行指令
            String[] instruction = {"X=1000","x++","x++","x--","!A100","end"};
            for(String ins : instruction){
                parseInstruction(ins);
            }
//        }
    }

    //检查PSW 判断是否需要中断处理
    private void checkPSW(){
        if((PSW & 0b001) == 0b001){
            //TODO: 程序结束中断
        }else if((PSW & 0b010) == 0b010){
            //TODO: 时间片结束中断
        }else if ((PSW & 0b100) == 0b100){
            //TODO: I/O中断
        }
    }

    //解析指令
    private void parseInstruction(String instruction){
        instruction = instruction.toLowerCase();
        //TODO: 解析指令
        if(instruction.startsWith("x=")){
            AX = Integer.parseInt(instruction.substring(2));
            System.out.println("AX = " + AX);
        }
        if(instruction.compareTo("x++")==0){
            AX++;
            System.out.println("AX++,AX = " + AX);
        }
        if(instruction.compareTo("x--")==0){
            AX--;
            System.out.println("AX--,AX = " + AX);
        }
        if(instruction.startsWith("!")){
            char deviceName =instruction.charAt(1);
            int requestTime = Integer.parseInt(instruction.substring(2));
            System.out.println("设备"+deviceName+" requestTime = " + requestTime);
        }
        if(instruction.compareTo("end")==0){
            System.out.println("程序结束 释放内存");
        }
    }


}
