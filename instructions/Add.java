package instructions;

import java.util.Stack;

public class Add implements IInstruction {
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        Integer argA = stack.pop();
        Integer argB = stack.pop();
        stack.push(argA + argB);

        System.out.println("--------");
        for (Integer integer : stack) {
            System.out.println(integer);
        }
        System.out.println("--------");
        System.out.println("FP: " + framePointer);
        System.out.println(argA + " + " + argB + " = " + (argA + argB));

        return programCounter + 1;
    }
}
