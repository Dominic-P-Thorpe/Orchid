package instructions;

import java.util.Stack;

public class Storei implements IInstruction {
    private Integer argument;


    public Storei(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        System.out.println("Here: " + argument / 4 + " - " + stack.peek().read());
        stack.set(framePointer + argument / 4, stack.pop());

        for (MemLocation l : stack) {
            System.out.println(l.read());
        }

        return programCounter + 1;
    }
}
