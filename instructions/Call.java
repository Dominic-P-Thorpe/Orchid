package instructions;

import java.util.Stack;

public class Call implements IInstruction {
    private Integer argument;


    public Call(Integer argument) {
        this.argument = argument;
    } 


    public Integer execute(Stack<Integer> stack, Integer framePointer, Integer programCounter) {
        stack.push(framePointer);
        stack.push(programCounter + 1);
        return argument / 4;
    }
}
