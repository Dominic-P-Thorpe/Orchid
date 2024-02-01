package instructions;

import java.util.Stack;

public class Storei implements IInstruction {
    private Integer argument;


    public Storei(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        System.out.println("Storing " + stack.peek() + " to " + (framePointer + argument / 4));
        stack.set(framePointer + argument / 4, stack.pop());
        return programCounter + 1;
    }
}
