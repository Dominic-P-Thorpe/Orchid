package instructions;

import java.util.Stack;

public class Jump extends IJump {
    public Jump(Integer argument) {
        super(argument);
    } 


    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        return this.argument / 4; // divide by 4 to reflect going from bytes to 32 bit instrs
    }
}
