package instructions;

import java.util.Stack;

public class Await implements IInstruction {
    public final Integer addr;


    public Await(Integer addr) {
        this.addr = addr;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
