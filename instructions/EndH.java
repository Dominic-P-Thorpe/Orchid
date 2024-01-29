package instructions;

import java.util.Stack;

public class EndH implements IInstruction {
    public final Integer funcToHandleAddr;


    public EndH(Integer funcToHandleAddr) {
        this.funcToHandleAddr = funcToHandleAddr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
