package ithaic.imitate_os.process;
import java.util.ArrayList;
import java.util.List;

public class InstructionValidator {

    // 支持的指令集
    private static final List<String> VALID_INSTRUCTIONS = List.of("x++", "x--", "x=", "!", "end");

    public static class SyntaxError {
        int lineNumber;
        int columnIndex;
        String message;

        SyntaxError(int lineNumber, int columnIndex, String message) {
            this.lineNumber = lineNumber;
            this.columnIndex = columnIndex;
            this.message = message;
        }

        @Override
        public String toString() {
            return "SyntaxError at line " + lineNumber + ", column " + columnIndex + ": " + message+"\n";
        }
    }

    public static List<String> validateAndGetInstructions(String program) throws Exception {
        List<SyntaxError> errors = new ArrayList<>();
        List<String> instructions = new ArrayList<>();
        String[] lines = program.split("\n");
        int lineNumber = 1;

        for (String line : lines) {
            String trimmedLine = line.trim().toLowerCase();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) { // 假设以 "//" 开头的行为注释
                lineNumber++;
                continue; // 跳过空行和注释行
            }
            //每行必须以分号结尾
            if (!trimmedLine.endsWith(";")) {
                errors.add(new SyntaxError(lineNumber, line.length() - trimmedLine.length() + 1, "Missing ';' at the end of line"));
            }

            String[] parts = trimmedLine.split(";");
            for (String part : parts) {
                String instruction = part.trim();
                if (!instruction.isEmpty()) {
                    //如果是!开头的指令,解析第一个?是否为A,B,C,解析第二个问号是否为数字,如果错误，提示哪里不合法
                    if(instruction.startsWith("!")) {
                        char name = instruction.charAt(1);
                        String number = instruction.substring(2);
                        if(name!='a' && name!='b' && name!='c') {
                            errors.add(new SyntaxError(lineNumber, part.indexOf(name) + 1, "Invalid deviceName:" + name));
                        }
                        //判断是否为数字,如果不是数字，提示哪里不合法,数字是否大于0
                        try {
                            int num = Integer.parseInt(number);
                            if(num<=0) {
                                errors.add(new SyntaxError(lineNumber, part.indexOf(number) + 1, "Invalid number(must be greater than 0):" + number));
                            }
                        } catch (NumberFormatException e) {
                            errors.add(new SyntaxError(lineNumber, part.indexOf(number) + 1, "Invalid number:" + number));
                        }
                        instructions.add(instruction);
                    }
                    //如果是x=开头的指令,解析?是否为数字,如果错误，提示哪里不合法
                    else if(instruction.startsWith("x=")) {
                        String number = instruction.substring(2);
                        try {
                            Integer.parseInt(number);
                        } catch (NumberFormatException e) {
                            errors.add(new SyntaxError(lineNumber, part.indexOf(number) + 1, "Invalid number:" + number));
                        }
                        instructions.add(instruction);
                    }
                    else if(VALID_INSTRUCTIONS.contains(instruction)){
                        instructions.add(instruction);
                    }else {
                        errors.add(new SyntaxError(lineNumber, part.indexOf(instruction) + 1, "Invalid instruction:" + instruction));
                    }
                }
            }
            lineNumber++;
        }

        // 检查程序是否以 "end" 结尾（作为整行，不考虑分号）
        if (!instructions.isEmpty() && !instructions.get(instructions.size() - 1).equals("end")) {
            errors.add(new SyntaxError(instructions.size(), 1, "Program must end with 'end'"));
        }

        // 如果存在错误，则返回错误列表；否则返回空列表和指令数组
        if (!errors.isEmpty()) {
            throw new Exception(errors.toString());
        } else {
            return instructions;
        }
    }

}