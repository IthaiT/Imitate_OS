package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import lombok.Getter;

import java.util.*;

public class DeviceManager {
    private static DeviceManager instance;
    private final Map<String, List<Device>> devices;
    @Getter
    private Queue<PCBInWaitingQueue> waitingQueue;

    private DeviceManager() {
        devices = new HashMap<>();
        List<Device> aDevices = new LinkedList<>();
        aDevices.add(new Device("A", "A1"));
        aDevices.add(new Device("A", "A2"));
        devices.put("A", aDevices);

        // 初始化B设备组，包含B1, B2, B3
        List<Device> bDevices = new LinkedList<>();
        bDevices.add(new Device("B", "B1"));
        bDevices.add(new Device("B", "B2"));
        bDevices.add(new Device("B", "B3"));
        devices.put("B", bDevices);

        // 初始化C设备组，包含C1, C2, C3
        List<Device> cDevices = new LinkedList<>();
        cDevices.add(new Device("C", "C1"));
        cDevices.add(new Device("C", "C2"));
        cDevices.add(new Device("C", "C3"));
        devices.put("C", cDevices);

        waitingQueue = new LinkedList<>();
    }

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }


    public void allocateDevice(PCB pcb, String deviceName, int requestTime) {
        deviceName = deviceName.toUpperCase();
        boolean flag = false;
        List<Device> devices1 = devices.get(deviceName);
        if (devices1 != null) {
            for (Device device : devices1) {
                if (device.getCurrentpcb() == null) {
                    device.allocate(pcb, requestTime);
                    flag = true;
                    break;
                }
            }
            //没有空闲设备
            if(!flag){
                PCBInWaitingQueue pcbInWaitingQueue = new PCBInWaitingQueue(deviceName, requestTime, pcb);
                waitingQueue.add(pcbInWaitingQueue);
            }
        } else {
            System.out.println("设备 " + deviceName + " 不存在");
        }
    }

    //返回HashMap， Key值为正在被使用设备， Value值为要使用那个设备的进程
    public Map<String, Integer> getUsedDeviceMessage() {
        Map<String, Integer> message = new HashMap<>();
        List<Device> tempA = devices.get("A");
        List<Device> tempB = devices.get("B");
        List<Device> tempC = devices.get("C");

        for (Device device : tempA) {
            if (device.getCurrentpcb() != null) {
                message.put(device.getDeviceID(), device.getCurrentpcb().getPid());
            }
        }
        for (Device device : tempB) {
            if (device.getCurrentpcb() != null) {
                String msg = "设备" + device.getDeviceID() + "正在被进程" + device.getCurrentpcb().getPid() + "使用";
                message.put(device.getDeviceID(), device.getCurrentpcb().getPid());
            }
        }

        for (Device device : tempC) {
            if (device.getCurrentpcb() != null) {
                String msg = "设备" + device.getDeviceID() + "正在被进程" + device.getCurrentpcb().getPid() + "使用";
                message.put(device.getDeviceID(), device.getCurrentpcb().getPid());
            }
        }

        if (message.isEmpty()) message.put("无设备被使用", 0);
        return message;
    }


    //返回HashMap， Key值为索求的设备， Value值为要使用设备的进程
    public Map<String, List<Integer>> getBlockedQueueMessage(){
        Map<String, List<Integer>> message = new HashMap<>();
        for (PCBInWaitingQueue pcbInWaitingQueue : waitingQueue) {
            //获取设备名字和pid
            String deviceName = pcbInWaitingQueue.getDeviceName();
            Integer pid = pcbInWaitingQueue.getPcb().getPid();
            //列表存在就存入，不存在新建一个再存入
            List<Integer> pidList = message.get(deviceName);
            if (pidList==null){
                pidList = new ArrayList<>();
                message.put(deviceName,pidList);
            }
            pidList.add(pid);
        }

        String msg = "当前阻塞队列为空";
        if(message.isEmpty()) message.put(msg, null);
        return message;
    }
}
