package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

@Data
public class Device {
    private String name;
    private int totalUnits;
    private int availableUnits;
    private Queue<PCB> waitingQueue;

    public Device(String name, int totalUnits) {
        this.name = name;
        this.totalUnits = totalUnits;
        this.availableUnits = totalUnits;
        this.waitingQueue = new LinkedList<>();
    }


    public boolean allocate(PCB pcb, int requestTime) {
        if (availableUnits > 0) {
            System.out.println("当前共" + availableUnits + "设备");
            availableUnits--;
            // 开进程记录设备使用倒计时
            new DeviceTimer(this, pcb, requestTime).start();
            return true;
        } else {
            waitingQueue.add(pcb);
            pcb.setBlockedReason("等待设备 " + name);
            pcb.setState(2);  // 进程阻塞
            System.out.println("设备 " + name + " 不可用，进程 " + pcb.getPid() + " 阻塞");
            return false;
        }
    }


    public void release() {
        availableUnits++;
        System.out.println("设备 " + name + " 释放");


        if (!waitingQueue.isEmpty()) {
            PCB nextPcb = waitingQueue.poll();
            allocate(nextPcb, nextPcb.getRunningTime());
        }
    }
}
