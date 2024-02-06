package instructions;

import java.util.Stack;

public class Gte implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop();
        Integer argB = stack.pop();
        if (argB >= argA)
            stack.push(1);
        else
            stack.push(0);
            
        return programCounter + 1;
    }
}
