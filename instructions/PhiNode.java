package instructions;

import java.util.Stack;

public class PhiNode implements IInstruction {
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        MemLocation locationB = stack.get(framePointer + stack.pop().read() / 4);
        MemLocation locationA = stack.get(framePointer + stack.pop().read() / 4);
        
        if (locationA.hasBeenWritten())
            stack.push(new MemLocation(locationB.read()));
        else 
            stack.push(new MemLocation(locationA.read()));
        
        return programCounter + 1;
    }
}
