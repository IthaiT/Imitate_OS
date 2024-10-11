package ithaic.imitate_os.deviceManager;

import ithaic.imitate_os.process.PCB;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DeviceManager {
    private static DeviceManager instance;
    @Getter
    private Map<String, Device> devices;

    private DeviceManager() {
        devices = new HashMap<>();
        devices.put("A", new Device("A", 2));
        devices.put("B", new Device("B", 3));
        devices.put("C", new Device("C", 3));
    }

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }


    public void allocateDevice(PCB pcb, String deviceName, int requestTime) {
        deviceName = deviceName.toUpperCase();
        Device device = devices.get(deviceName);
        if (device != null) {
            device.allocate(pcb, requestTime);
        } else {
            System.out.println("设备 " + deviceName + " 不存在");
        }
    }


}
