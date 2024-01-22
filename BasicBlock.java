import java.util.ArrayList;
import java.util.Stack;

import instructions.IInstruction;
import instructions.MemLocation;

public class BasicBlock implements IInstruction {
    public final Integer startAddr;

    private ArrayList<IInstruction> contents;
    private ArrayList<BasicBlock> entryAddr = new ArrayList<BasicBlock>();
    private ArrayList<BasicBlock> exitAddrs = new ArrayList<BasicBlock>();
    private ArrayList<Integer> locationsWrittenTo;


    /**
     * Constructor for the BasicBlock class, entry and exit points are initialized to empty lists
     * @param startAddr The address in the original source code of the first instruction in the basic block 
     * @param contents The instructions in the basic block as IInstructions
     * @param locationsWrittenTo The locations in memory (on the stack) which the basic block modifies
     */
    public BasicBlock(Integer startAddr, ArrayList<IInstruction> contents, ArrayList<Integer> locationsWrittenTo) {
        this.startAddr = startAddr;
        this.contents = contents;
        this.locationsWrittenTo = locationsWrittenTo;
    }


    /**
     * Executes the contents of the basic block, and will transfer execution to another basic block
     * at the end, or will terminate execution of the program if there is no continuation in the 
     * control flow graph.
     * @param stack The current state of the stack at the start of the execution of the basic block
     * @param framePointer Pointer to the address on the stack which is the start of the current stack frame
     */
    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        Integer newPC = programCounter;
        for (IInstruction instr : contents) {
            newPC = instr.execute(stack, framePointer, newPC);
        }

        return newPC;
    } 


    public Integer getStartAddr() {
        return startAddr;
    }


    public ArrayList<IInstruction> getContents() {
        return contents;
    }


    public ArrayList<BasicBlock> getEntrypoint() {
        return entryAddr;
    }


    public void addEntrypoint(BasicBlock entryPoint) {
        entryAddr.add(entryPoint);
    }


    public ArrayList<BasicBlock> getExitpoints() {
        return exitAddrs;
    }


    public void addExitpoint(BasicBlock exitPoint) {
        exitAddrs.add(exitPoint);
    }


    public ArrayList<Integer> getLocationsWrittenTo() {
        return locationsWrittenTo;
    }


    public void printBlock() {
        System.out.println(this.startAddr  + ": ");

        System.out.print("Enters from: ");
        for (BasicBlock entryPoint : entryAddr) {
            System.out.print(entryPoint.startAddr + ", ");
        }
        System.out.print("\n");

        for (IInstruction instruction : contents) {
            System.out.println(instruction);
        }

        System.out.print("Jumps to: ");
        for (BasicBlock exitPoint : exitAddrs) {
            System.out.print(exitPoint.startAddr + ", ");
        }

        System.out.print("\n");
    }
}
