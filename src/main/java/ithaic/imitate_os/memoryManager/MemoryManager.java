package ithaic.imitate_os.memoryManager;

import lombok.Getter;

public class MemoryManager {
    private final int MEMORY_SIZE = 512;
    private Memory memory;

    @Getter
    private static MemoryManager instance;

    static{
        instance = new MemoryManager();
    }

    public MemoryManager() {
        this.memory = Memory.getInstance();
    }

    /**
     * 该函数分配内存
     * @param size 申请的内存块大小
     * @return 返回内存块
     */
    public MemoryBlock allocate(int size) {
        //查找内存分配表，采用首次适配算法
        MemoryBlock temp = memory.getMemoryBlock();
        while (temp!= null) {
            //找到足够大的空闲内存块
            if(temp.isFree()&&temp.getSize()>=size){
                //创建新的内存块，将剩余的大小设置为true，表示空闲
                MemoryBlock newBlock = null;
                if(temp.getAddress()+size<MEMORY_SIZE) {
                    newBlock = new MemoryBlock(temp.getAddress() + size, temp.getSize() - size, true, temp.getNext(), temp);
                }
                //将原空闲内存块的大小设置为申请的大小
                temp.setSize(size);
                //将原空闲块状态设置为false，表示已分配
                temp.setFree(false);
                //将新的内存块插入到链表中
                temp.setNext(newBlock);
                return temp;
            }
            temp = temp.getNext();
        }
        System.out.println("内存不足");
        return null;
    }

    /**
     * 该函数释放PCB所占用的内存块
     * @param releaseBlock 需要释放的内存块
     */
    public void release(MemoryBlock releaseBlock) {
        //将内存块设置为true
        releaseBlock.setFree(true);
        //合并相邻的空闲内存块
        if(releaseBlock.getNext()!=null&&releaseBlock.getNext().isFree()){
            MemoryBlock next = releaseBlock.getNext();
            releaseBlock.setSize(releaseBlock.getSize()+next.getSize());
            releaseBlock.setNext(next.getNext());
        }
        if(releaseBlock.getPre()!=null&&releaseBlock.getPre().isFree()){
            MemoryBlock pre = releaseBlock.getPre();
            pre.setSize(pre.getSize()+releaseBlock.getSize());
            pre.setNext(releaseBlock.getNext());

            if(pre.getNext()!=null)pre.getNext().setPre(pre);
        }
    }

    /**
     * 该函数向内存中写入数据
     * @param block PCB所占用的内存块
     * @param content 写入的数据
     */
    public void write(MemoryBlock block, char[] content) {
        if(block==null)return;
        int startAddress = block.getAddress();
        int size = block.getSize();
        char[] userMemory = memory.getUserMemory();
        for(int i=startAddress,j=0; i<startAddress+size; i++,j++) {
            if(j>=content.length)break;
            userMemory[i] = content[j];
        }
    }

    /**
     * 该函数从内存中读取指令
     * @param block PCB所占用的内存块
     * @return 指令数组
     */
    public String fetchInstruction(MemoryBlock block) {
        if(block==null)return null;
        //从内存中读取指令
        int startAddress = block.getAddress();
        int size = block.getSize();
        char[] instructions = new char[size];
        char[] userMemory = memory.getUserMemory();
        for(int i=startAddress,j=0; i<startAddress+size; i++,j++) {
            instructions[j] = userMemory[i];
        }
        return String.valueOf(instructions);
    }


}
