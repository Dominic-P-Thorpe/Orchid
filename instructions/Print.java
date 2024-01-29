package instructions;

import java.util.Stack;

public class Print implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        System.out.println("Printing");
        return programCounter + 1;
    }
}
