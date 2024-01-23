package instructions;

import java.util.Stack;

public class Ret implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer returnValue = stack.pop();
        Integer returnAddr = stack.get(framePointer + 1);
        
        Stack<Integer> newStack = new Stack<Integer>();
        for (int i = 0; i < framePointer + 1; i++) {
            newStack.push(stack.get(i));
        }
        
        stack.clear();
        for (int i = 0; i < newStack.size(); i++) {
            stack.push(newStack.get(i));
        }

        stack.push(returnValue);

        return returnAddr;
    }
}
