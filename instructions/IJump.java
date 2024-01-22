package instructions;

import java.util.Stack;

public abstract class IJump implements IInstruction {
    protected Integer argument;


    public IJump(Integer argument) {
        this.argument = argument;
    }


    public Integer getArgument() {
        return argument;
    }


    public abstract Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter);
}
