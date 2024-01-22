package instructions;

import java.util.Stack;

public class Gt implements IInstruction {
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop().read();
        Integer argB = stack.pop().read();
        if (argB > argA)
            stack.push(new MemLocation(1));
        else
            stack.push(new MemLocation(0));
            
        return programCounter + 1;
    }
}
