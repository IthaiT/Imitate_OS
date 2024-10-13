package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import ithaic.imitate_os.process.ProcessManager;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

@Data
public class Device {
    private String name;
    private String deviceID;
    private PCB currentpcb;
    private int totalUnits;
    private int availableUnits;
    private Queue<PCB> waitingQueue;

    public Device(String name, String ID) {
        this.name = name;
        this.deviceID = ID;
        this.availableUnits = totalUnits;
        this.waitingQueue = new LinkedList<>();
    }


    public boolean allocate(PCB pcb, int requestTime) {
        if (currentpcb == null) {
            currentpcb = pcb;

            // 开进程记录设备使用倒计时
            new DeviceTimer(this, currentpcb, requestTime).start();
            System.out.println(DeviceManager.getInstance().getUsedDeviceMessage());
            System.out.println(DeviceManager.getInstance().getBlockedQueueMessage());
            return true;
        } else {
            ProcessManager.getInstance().block(pcb);  // 进程阻塞
            System.out.println("设备 " + name + " 不可用，进程 " + pcb.getPid() + " 阻塞");
            return false;
        }


    }


    public void release() {
        currentpcb = null;
        System.out.println("设备 " + deviceID + " 释放");
        if (!DeviceManager.getInstance().getWaitingQueue().isEmpty()) {
            PCBInWaitingQueue pcbInWaitingQueue = DeviceManager.getInstance().getWaitingQueue().peek();
            if(pcbInWaitingQueue.getDeviceName().equals(name))
            {
                allocate(pcbInWaitingQueue.getPcb(), pcbInWaitingQueue.getRequestTime());
                DeviceManager.getInstance().getWaitingQueue().poll();
            }

        }
    }
}
