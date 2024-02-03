package instructions;

import java.util.Stack;

public class EndP implements IInstruction {
    public final Integer funcToHandleAddr;


    public EndP(Integer funcToHandleAddr) {
        this.funcToHandleAddr = funcToHandleAddr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
