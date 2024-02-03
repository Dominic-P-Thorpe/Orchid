package instructions;

import java.util.Stack;

public class Eq implements IInstruction{
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop();
        Integer argB = stack.pop();
        stack.push(argA == argB ? 1 : 0);

        return programCounter + 1;
    }
}
