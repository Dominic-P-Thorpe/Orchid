package instructions;

import java.util.Stack;

public class Div implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop();
        Integer argB = stack.pop();
        stack.push(argB / argA);

        return programCounter + 1;
    }
}
