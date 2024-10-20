package ithaic.imitate_os.fileManager;

import lombok.Getter;

public class HistoryCommand {
    @Getter
    private static HistoryCommand instance;

    private class Command{
        private String command;
        private Command next;
        private Command pre;

        public Command(String command,Command pre){
            this.command = command;
            this.pre = pre;
            this.next = null;
        }
    }

    private Command cur;
    private Command tail;

    static {
        instance = new HistoryCommand();
    }

    public HistoryCommand(){
        cur = null;
        tail = null;
    }

    /**
     * 该函数在命令历史记录中添加一个命令
     * @param command 所要存储的命令
     */

    public void addCommand(String command){
        if(command.isEmpty()||command.equals(" ")){
            return;
        }
        if(tail == null){
            Command newCommand = new Command(command,null);
            tail = newCommand;
            cur = newCommand;
        }
        Command newCommand = new Command(command,tail);
        tail.next = newCommand;
        tail = newCommand;
        cur = newCommand;
    }

    /**
     * 该函数获取命令历史记录中的命令
     * @param opCode 0表示向上键，1表示向下键
     * @return 返回取到的命令
     */

    public String getCommand(int opCode){
        if(cur==null){
            return null;
        }
        if(!FileInteract.getCommandInput().getText().equals(cur.command)){
            return cur.command;
        }
        if(opCode == 0&&cur.pre != null){
            cur = cur.pre;
        }else if(opCode == 1&&cur.next != null){
            cur = cur.next;
        }
        return cur.command;
    }
}
