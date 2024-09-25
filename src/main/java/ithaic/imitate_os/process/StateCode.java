package ithaic.imitate_os.process;

public class StateCode {
    public static final int READY = 0;
    public static final int RUNNING = 1;
    public static final int BLOCKED = 2;

    //防止实例化
    private StateCode() {}
}
