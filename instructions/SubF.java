package instructions;

import java.util.Stack;

public class SubF implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Float argA = Float.intBitsToFloat(stack.pop());
        Float argB = Float.intBitsToFloat(stack.pop());
        stack.push(Float.floatToIntBits(argB - argA));

        return programCounter + 1;
    }
}
