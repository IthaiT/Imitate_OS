package ithaic.imitate_os.process;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPU {
    private static PCB runningProcess;
    private static String IR;
    private static int PC;
    private static char PSW = 0b000;
    int AX;

    //模拟CPU运行
    public void run(){
        while(true){
            checkPSW();
            //TODO: 解析指令，执行指令
        }
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
    private void parseInstruction(){
        //TODO: 解析指令
    }


}
