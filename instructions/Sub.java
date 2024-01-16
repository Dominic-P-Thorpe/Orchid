package instructions;

import java.util.Stack;

public class Sub implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop();
        Integer argB = stack.pop();
        stack.push(argA - argB);

        return programCounter + 1;
    }
}
