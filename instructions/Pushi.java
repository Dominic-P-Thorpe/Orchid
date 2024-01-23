package instructions;

import java.util.Stack;

public class Pushi implements IInstruction {
    private Integer argument;
    public Pushi(Integer argument) {
        this.argument = argument;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        stack.push(argument);
        return programCounter + 1;
    }
}
