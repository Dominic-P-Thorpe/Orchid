package instructions;

import java.util.Stack;

public class Jnzro extends IJump {
    public Jnzro(Integer argument) {
        super(argument);
    } 


    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        if (stack.pop().read() != 0)
            return this.argument / 4; // divide by 4 to reflect going from bytes to 32 bit instrs
        else
            return programCounter + 1;
    }
}
