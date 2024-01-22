package instructions;

import java.util.Stack;

public class Nop implements IInstruction {
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
