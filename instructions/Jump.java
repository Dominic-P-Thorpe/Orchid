package instructions;

import java.util.Stack;

public class Jump implements IInstruction {
    private Integer argument;


    public Jump(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return this.argument / 4; // divide by 4 to reflect going from bytes to 32 bit instrs
    }
}
