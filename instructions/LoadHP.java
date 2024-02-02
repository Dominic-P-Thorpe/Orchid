package instructions;

import java.util.Stack;

public class LoadHP implements IInstruction {
    public final Integer address;


    public LoadHP(Integer address) {
        this.address = address;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
