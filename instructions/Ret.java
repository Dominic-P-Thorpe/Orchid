package instructions;

import java.util.Stack;

public class Ret implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer returnValue = stack.pop();
        Integer returnAddr = stack.get(framePointer);
        stack.push(returnValue);
        return returnAddr;
    }
}
