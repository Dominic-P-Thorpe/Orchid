package instructions;

import java.util.Stack;

public class Storei implements IInstruction {
    private Integer argument;


    public Storei(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        int value = stack.pop();
        stack.set(framePointer + argument / 4, value);
        return programCounter + 1;
    }
}
