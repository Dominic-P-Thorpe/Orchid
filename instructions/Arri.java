package instructions;

import java.util.Stack;

public class Arri implements IInstruction {
    public final Integer length;


    public Arri(Integer length) {
        this.length = length;
    }


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        return programCounter + 1;
    }
}
