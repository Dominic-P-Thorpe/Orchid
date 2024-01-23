package instructions;

import java.util.Stack;

public class JZro implements IInstruction {
    private Integer argument;


    public JZro(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        if (stack.pop() == 0)
            return this.argument / 4; // divide by 4 to reflect going from bytes to 32 bit instrs
        else
            return programCounter + 1;
    }
}
