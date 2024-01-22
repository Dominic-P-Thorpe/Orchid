package instructions;

import java.util.Stack;

public class Mult implements IInstruction {
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop().read();
        Integer argB = stack.pop().read();
        stack.push(new MemLocation(argA * argB));

        return programCounter + 1;
    }
}
