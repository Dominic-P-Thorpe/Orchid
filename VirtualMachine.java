import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import instructions.Call;
import instructions.EndH;
import instructions.IInstruction;
import instructions.LoadHP;
import instructions.StartH;

/**
 * A singleton class which takes a program binary in Orchid bytecode and can execute that code
 * and display the result of running the program. Simulates the program counter, frame pointer
 * and the stack.
 */
public class VirtualMachine {
    private static VirtualMachine vm = null;
    private Integer programCounter = 0;
    private final ArrayList<CritcalSection> criticalSections = new ArrayList<CritcalSection>();
    private final HashMap<Integer, HandlerData> activeHandlers = new HashMap<Integer, HandlerData>(); // function to handle -> handler
    private final Stack<Integer> stack = new Stack<Integer>();
    private final Stack<Integer> handlerArgsStack = new Stack<Integer>();
    private final Stack<Integer> framePointerStack = new Stack<Integer>();
    private final HashMap<Integer, MemoryItem> memory;
    private final ArrayList<IInstruction> program;
    
    public static Integer framePointer = 0;


    /**
     * Private constructor for this class due to Singletop design pattern. Translates the 
     * bytecode program in the byte array into IInstructions and puts them into the program
     * memory for execution. 
     * @param programBytes The program binary this VM will run
     */
    private VirtualMachine(byte[] programBytes) {
        Parser parser = new Parser();
        parser.parse(programBytes);
        memory = parser.getDataSection();
        program = parser.getProgram();

        getCriticalSections();
    }


    /**
     * Part of the singleton pattern this class uses. Creates a new instance of the VM and
     * returns it, or returns the existing VM instance if one has already been created
     * beforehand.
     * @param programBytes The program binary the VM will run (ignored if no new instance created)
     * @return The singleton instance of the virtual machine
     */
    public static VirtualMachine getInstance(byte[] programBytes) {
        if (vm == null)
            VirtualMachine.vm = new VirtualMachine(programBytes);
        return VirtualMachine.vm;
    }


    /**
     * Executes the current program in the program memory. Will terminate when the program
     * counter's value is outside the range 0 - 0x00FFFFFE, which is the signal to halt the
     * execution. Prints the stack after execution.
     */
    public void execute() {
        while (programCounter >= 0 && programCounter < 0x00FFFFFF) {
            Integer newFramePointer = framePointer;
            
            IInstruction instruction = this.program.get(programCounter);
            programCounter = instruction.execute(stack, framePointer, programCounter);
            if (instruction instanceof instructions.Call) {
                Call callInstr = (instructions.Call)instruction;
                Integer numArguments = callInstr.paramCount;
                newFramePointer = stack.size() - 2 - numArguments;
            }

            else if (instruction instanceof instructions.Ret) {
                newFramePointer = stack.remove(stack.size() - 2); // remove and return 2nd from top elem on stack
            }

            else if (instruction instanceof instructions.StartH) {
                StartH startH = (StartH)instruction;
                HandlerData handlerData = new HandlerData(startH.handlerAddr, framePointer);
                activeHandlers.put(startH.funcToHandleAddr, handlerData);
            } else if (instruction instanceof instructions.EndH) {
                EndH endH = (EndH)instruction;
                activeHandlers.remove(endH.funcToHandleAddr);
            }

            else if (instruction instanceof instructions.Print) {
                if (activeHandlers.containsKey(0xFFFFFFF0)) {
                    handlerArgsStack.push(stack.pop()); // transfer ordering arg to handler args stack
                    handlerArgsStack.push(stack.pop()); // transfer msg ptr arg to handler args stack
                    stack.push(programCounter); // push a pseudo return pointer
                    programCounter = activeHandlers.get(0xFFFFFFF0).handlerAddr / 4;

                    // during the handler, restore the frame pointer back to where it should be
                    // for the function containing the handler
                    framePointerStack.push(framePointer);
                    newFramePointer = activeHandlers.get(0xFFFFFFF0).framePtr;
                } else {
                    stack.pop(); // dont care about the ordering param on top of the stack
                    System.out.println(memory.get(stack.pop()).getContents());
                }
            }

            else if (instruction instanceof instructions.RetH) {
                newFramePointer = framePointerStack.pop();
            }

            else if (instruction instanceof instructions.LoadHP) {
                LoadHP loadHP = (LoadHP)instruction;
                stack.set(framePointer + loadHP.address / 4, handlerArgsStack.pop());
            }

            framePointer = newFramePointer;
        }
        
        printStack();
    }


    /**
     * Initializes the critical sections field of the virtual machine by iterating through the
     * loaded program and maintains a stack of the addresses of Lock instructions as it goes, 
     * pairing them with Unlock instructions to get the address range within which the lock is
     * effective. 
     */
    private void getCriticalSections() {
        Stack<Integer> startAddressesStack = new Stack<Integer>();
        for (int i = 0; i < program.size(); i++) {
            if (program.get(i) instanceof instructions.Lock) {
                startAddressesStack.push(i);
            } else if (program.get(i) instanceof instructions.Unlock) {
                Integer startAddr = startAddressesStack.pop();
                criticalSections.add(new CritcalSection(startAddr, i));
            }
        }
    }


    /** Prints the current context of the stack */
    public void printStack() {
        stack.forEach(System.out::println);
    }
}
