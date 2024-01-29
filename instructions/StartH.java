package instructions;

import java.util.Stack;

public class StartH implements IInstruction {
    public final Integer numArgs;
    public final Integer funcToHandleAddr;
    public final Integer handlerAddr;


    public StartH(Integer numArgs, Integer funcToHandleAddr, Integer handlerAddr) {
        this.numArgs = numArgs;
        this.funcToHandleAddr = funcToHandleAddr;
        this.handlerAddr = handlerAddr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
