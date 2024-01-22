package instructions;

import java.util.Stack;

public class Ret implements IInstruction {
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        Integer returnValue = stack.pop().read();
        Integer returnAddr = stack.get(framePointer).read();
        stack.push(new MemLocation(returnValue));
        return returnAddr;
    }
}
