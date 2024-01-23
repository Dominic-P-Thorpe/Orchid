package instructions;

import java.util.Stack;

public class Call implements IInstruction {
    /** 
     * The number of parameters the function to be called takes. This many items will be popped 
     * from the top of the stack and inserted into the variables section of the called function
     * at the start of its execution.
     */
    public final Integer paramCount;
    /** The address of the first instruction in the callee function. */
    public final Integer targetAddr;


    /**
     * Creates an instance of the Call instruction, which is a double word instruction which has 2
     * arguments, paramCount in the 1st word, and targetAddr in the 2nd.
     * @param paramCount The number of parameters the callee function takes
     * @param targetAddr The address of the first instruction of the callee function
     */
    public Call(Integer paramCount, Integer targetAddr) {
        this.paramCount = paramCount;
        this.targetAddr = targetAddr;
    } 


    /**
     * Executes the Call instruction, causing control to move to the callee function. The frame 
     * pointer is changed outside this function in the VirtualMachine class to point to the start
     * of the new stack frame.
     * 
     * @param stack The stack at the time this instruction is executed. Will be modified by this instruction
     * @param framePointer Pointer to the start of the current frame in the stack
     * @param programCounter Pointer to the instruction being executed in the program
     * @return The new program counter pointing to the next instruction to execute after this one 
     * @see VirtualMachine
     */
    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        // pop the arguments to the function off the stack in the current call frame and add them
        // to a vector
        Stack<Integer> arguments = new Stack<Integer>();
        for (int i = 0; i < paramCount; i++) {
            arguments.push(stack.pop());
        }
        
        // push the old frame pointer and program counter to the stack so they can be restored later
        stack.push(framePointer);
        stack.push(programCounter + 1);

        while (!arguments.isEmpty()) {
            stack.push(arguments.pop());
        }

        return targetAddr / 4;
    }
}
