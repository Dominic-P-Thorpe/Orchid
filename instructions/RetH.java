package instructions;

import java.util.Stack;

public class RetH implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return stack.pop();
    }
}
