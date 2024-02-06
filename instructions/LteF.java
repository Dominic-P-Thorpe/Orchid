package instructions;

import java.util.Stack;

public class LteF implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Float argA = Float.intBitsToFloat(stack.pop());
        Float argB = Float.intBitsToFloat(stack.pop());
        if (argB <= argA)
                stack.push(1);
            else
                stack.push(0);
                
        return programCounter + 1;
    }
}
