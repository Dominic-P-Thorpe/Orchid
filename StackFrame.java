import java.util.HashMap;
import java.util.Stack;

public class StackFrame {
    /** The index of this frame on the call stack. */
    public final Integer id;
    /** The location in the program code to return to when the stack frame ends, null if the program should end. */
    public final Integer returnPtr;

    private HashMap<Integer, MemoryItem> variables = new HashMap<Integer, MemoryItem>();
    private HashMap<String, Integer> handlers = new HashMap<String, Integer>(); 
    private Stack<Integer> stack = new Stack<Integer>(); 


    public StackFrame(Integer id, Integer returnPtr) {
        this.id = id;
        this.returnPtr = returnPtr;
    }


    public MemoryItem getVariable(Integer address) {
        return variables.get(address);
    }


    public void addVariable(Integer address, MemoryItem item) {
        variables.put(address, item);
    }


    public void removeVariable(Integer address) {
        variables.remove(address);
    }


    public Integer getHandlerPtr(String funcToHandle) {
        return handlers.get(funcToHandle);
    }


    public void addHandler(String funcToHandle, Integer handlerPtr) {
        handlers.put(funcToHandle, handlerPtr);
    }


    public void removeHandler(String funcToHandle) {
        handlers.remove(funcToHandle);
    }


    public Integer pop() {
        return stack.pop();
    }
}
