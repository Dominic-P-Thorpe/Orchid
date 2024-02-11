package instructions;

import java.util.Stack;

public class Neg implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer item = stack.pop();
        stack.push(-item);    
        return programCounter + 1;
    }
}
