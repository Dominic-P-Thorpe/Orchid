package instructions;

import java.util.Stack;

public class Map implements IInstruction {
    /** 
     * The number of parameters the function to be called takes. This many items will be popped 
     * from the top of the stack and inserted into the variables section of the called function
     * at the start of its execution.
     */
    public final Integer paramCount;
    /** The address of the first instruction in the callee function. */
    public final Integer targetAddr;


    /**
     * Creates an instance of the Map instruction, which is a double word instruction which has 2
     * arguments, paramCount of the anonymous function in the 1st word, and targetAddr in the 2nd.
     * @param paramCount The number of parameters the callee function takes
     * @param targetAddr The address of the first instruction of the callee function
     */
    public Map(Integer paramCount, Integer targetAddr) {
        this.paramCount = paramCount;
        this.targetAddr = targetAddr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
