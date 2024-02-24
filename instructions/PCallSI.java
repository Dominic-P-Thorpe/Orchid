package instructions;

import java.util.Stack;

public class PCallSI implements IInstruction {
    public final Integer numArgs;
    public final Integer funcStartAddr;
    public final Integer destinationAddr;


    public PCallSI(Integer numArgs, Integer funcStartAddr, Integer destinationAddr) {
        this.numArgs = numArgs;
        this.funcStartAddr = funcStartAddr;
        this.destinationAddr = destinationAddr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
