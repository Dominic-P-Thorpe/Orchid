package instructions;

import java.util.Stack;

public class ArrRng implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {            
        return programCounter + 1;
    }
}
