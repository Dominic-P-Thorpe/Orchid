package instructions;

import java.util.Stack;

public class Pushi implements IInstruction {
    private Integer argument;
    public Pushi(Integer argument) {
        this.argument = argument;
    }


    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        stack.push(new MemLocation(argument));
        return programCounter + 1;
    }
}
