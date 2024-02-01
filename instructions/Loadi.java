package instructions;

import java.util.Stack;

public class Loadi implements IInstruction {
    private Integer argument;


    public Loadi(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        System.out.println("Loading " + stack.get(framePointer + argument / 4) + " from " + (framePointer + argument / 4));
        stack.push(stack.get(framePointer + argument / 4));
        return programCounter + 1;
    }
}
