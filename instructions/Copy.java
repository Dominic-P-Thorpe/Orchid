package instructions;

import java.util.Stack;

public class Copy implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        stack.push(stack.peek());
        return programCounter + 1;
    }
}
