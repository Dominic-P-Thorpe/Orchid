package instructions;

import java.util.Stack;

public class PushF implements IInstruction {
    private Integer argument;


    /**
     * Constructs a new PushF instruction which implements the IInstruction interface
     * @param argument A 32-bit float, with its bit pattern encoded into an integer so it can work
     * with the stack.
     */
    public PushF(Integer argument) {
        this.argument = argument;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        stack.push(argument);
        return programCounter + 1;
    }
}
