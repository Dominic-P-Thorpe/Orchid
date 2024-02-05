package instructions;

import java.util.Stack;

public class MultF implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Float argA = Float.intBitsToFloat(stack.pop());
        Float argB = Float.intBitsToFloat(stack.pop());
        stack.push(Float.floatToIntBits(argA * argB));

        return programCounter + 1;
    }
}
