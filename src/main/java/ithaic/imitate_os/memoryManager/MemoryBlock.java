package ithaic.imitate_os.memoryManager;

import lombok.Data;

@Data
public class MemoryBlock {
    private int address;
    private int size;
    private boolean isFree;
    private MemoryBlock next;
    private MemoryBlock pre;

    public MemoryBlock(int address, int size,boolean isFree, MemoryBlock next,MemoryBlock pre) {
        this.address = address;
        this.size = size;
        this.isFree = isFree;
        this.next = next;
        this.pre = pre;
    }

    @Override
    public String toString() {
        return "MemoryBlock";
    }
}
