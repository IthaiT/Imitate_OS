package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import ithaic.imitate_os.process.ProcessManager;

public class DeviceTimer extends Thread{
    private Device device;
    private PCB pcb;
    private int requestTime;

    public DeviceTimer(Device device, PCB pcb, int requestTime) {
        this.device = device;
        this.pcb = pcb;
        this.requestTime = requestTime;
    }

    @Override
    public void run() {
        try {
            System.out.println("设备 " + device.getName() + " 分配给进程 " + pcb.getPid());
            // 模拟设备使用倒计时
            System.out.println("进程 " + pcb.getPid() + " 使用设备 " + device.getName() + " " + requestTime + " 单位时间");
            Thread.sleep(requestTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        device.release();

        // 设备用完后唤醒等待队列中的进程
        ProcessManager.getInstance().awake(pcb);
        System.out.println("进程 " + pcb.getPid() + " 设备使用完成，继续执行其他操作");
    }
}
