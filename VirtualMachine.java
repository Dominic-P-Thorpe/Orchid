import java.util.ArrayList;
import java.util.Stack;

import instructions.IInstruction;


/**
 * A singleton class which takes a program binary in Orchid bytecode and can execute that code
 * and display the result of running the program. Simulates the program counter, frame pointer
 * and the stack.
 */
public class VirtualMachine {
    private static VirtualMachine vm = null;
    private Integer programCounter = 0;
    private Integer framePointer = 0;
    private final Stack<Integer> stack = new Stack<Integer>();
    private final ArrayList<IInstruction> program = new ArrayList<IInstruction>();


    /**
     * Private constructor for this class due to Singletop design pattern. Translates the 
     * bytecode program in the byte array into IInstructions and puts them into the program
     * memory for execution. 
     * @param programBytes The program binary this VM will run
     */
    private VirtualMachine(byte[] programBytes) {
        int instruction = 0;
        for (int i = 0; i < programBytes.length; i += 4) {
            instruction = (programBytes[i] & 0xFF) << 24 | (programBytes[i + 1] & 0xFF) << 16 | 
                            (programBytes[i + 2] & 0xFF) << 8 | (programBytes[i + 3] & 0xFF);
            this.program.add(getInstructionFromOpcode(instruction));
        }
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
            System.out.println(this.program.get(programCounter));
            programCounter = this.program.get(programCounter).execute(stack, framePointer, programCounter);
        }
        
        printStack();
    }


    /**
     * Takes a 32-bit instruction and returns the instruction translated to an IInstruction
     * which can be executed and put into program memory.
     * @param instruction The instruction binary to convert to an IInstruction
     * @return The translated IInstruction
     * @see IInstruction
     */
    public IInstruction getInstructionFromOpcode(int instruction) {
        int operand = instruction >> 24;
        int argument = instruction & 0x00FFFFFF;
        switch (operand) {
            case 0x00: return new instructions.Nop();
            case 0x01: return new instructions.Add();
            case 0x02: return new instructions.Sub();
            case 0x03: return new instructions.Mult();
            case 0x04: return new instructions.Div();
            case 0x0A: return new instructions.Gt();
            case 0x0E: return new instructions.Ret();
            case 0x12: return new instructions.Pushi(argument);
            case 0x13: return new instructions.Loadi(argument);
            case 0x14: return new instructions.Storei(argument);
            case 0x16: return new instructions.Jump(argument);
            case 0x17: return new instructions.JZro(argument);
            default: return null;
        }
    }


    /** Prints the current context of the stack */
    public void printStack() {
        stack.forEach(System.out::println);
    }
}
