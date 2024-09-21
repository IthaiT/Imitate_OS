package ithaic.imitate_os.process;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PCB {
    private int pid;
    private String state;
    private String[] instructions;
    private int PC;
    private char PSW;
    int AX;
}
