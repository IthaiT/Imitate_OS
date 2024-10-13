package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import lombok.Data;

@Data
public class PCBInWaitingQueue {
    private String deviceName;
    private int requestTime;
    private PCB pcb;

    public PCBInWaitingQueue(String deviceName, int requestTime, PCB pcb)
    {
        this.deviceName = deviceName;
        this.requestTime = requestTime;
        this.pcb = pcb;
    }
}
