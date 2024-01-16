package instructions;

import java.util.Stack;

public interface IInstruction {
    /**
     * Takes information about the current state of the virtual CPU and uses
     * that to execute the instruction, which may modify the contents of the 
     * stack and of RAM.
     * @param stack The current state of the stack
     * @param framePointer The address of the first byte on the stack of the current stack frame
     * @param programCounter The current value of the program counter
     * @return The new value of the program counter
     */
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter);
}
