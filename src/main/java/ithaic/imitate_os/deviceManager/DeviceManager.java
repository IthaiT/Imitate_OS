package ithaic.imitate_os.deviceManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class DeviceManager {
    //设备容量，等待，使用中。
    private HashMap<String,Integer> deviceCapacity;
    private HashMap<String, Queue<String>> deviceWaiting;
    private HashMap<String,String> deciveBeingUsed;
    public DeviceManager(){
        deviceCapacity=new HashMap<>();
        deviceWaiting=new HashMap<>();
        deciveBeingUsed=new HashMap<>();

        //初始化设备容量
        deviceCapacity.put("A",2);
        deviceCapacity.put("B",3);
        deviceCapacity.put("C",3);
        //初始化等待队列
        deviceWaiting.put("A",new LinkedList<>());
        deviceWaiting.put("B",new LinkedList<>());
        deviceWaiting.put("C",new LinkedList<>());
        //初始化使用中队列
        deciveBeingUsed.put("A",null);
        deciveBeingUsed.put("B",null);
        deciveBeingUsed.put("C",null);
    }


    //申请设备,同步synchronized
    public synchronized void requestDevice(String deviceName, String processName){
        //判断设备是否可用
        if(deviceCapacity.get(deviceName)>0){
            deviceCapacity.put(deviceName,deviceCapacity.get(deviceName)-1);
            deciveBeingUsed.put(deviceName,processName);

            System.out.println(processName+"申请并占用"+deviceName+"设备。");
            //System.out.println(deviceName+"现在队列为"+deciveCapacity.get(deviceName));
        }else{

            //设备不可用，加入等待队列
            deviceWaiting.get(deviceName).add(processName);
            System.out.println(processName+"无法分配到"+deviceName+"设备。进入等待队列。");
            System.out.println("等待队列长度为"+deviceWaiting.get(deviceName).size());

            try{
                //阻塞并等待
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    //释放设备，同步synchronized
    public synchronized void releaseDevice(String deviceName,String processName){
        //先释放
        deviceCapacity.put(deviceName,deviceCapacity.get(deviceName)+1);
        //需要检查等待队列
        Queue<String> queue = deviceWaiting.get(deviceName);
        if (!queue.isEmpty()){
            String nextProcess = queue.poll();
            deciveBeingUsed.put(deviceName,nextProcess);
           // System.out.println(nextProcess+"从等待队列中取出"+deviceName+"设备。");
            //System.out.println("等待队列长度为"+deviceWaiting.get(deviceName).size());
            //唤醒等待队列进程
            notifyAll();
        }else{
            deciveBeingUsed.put(deviceName,null);
            System.out.println(processName+"释放"+deviceName+"设备。");
        }

    }
    public void displayStatus(){
        //显示设备状态
    }

}
