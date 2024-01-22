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


    public BasicBlock(Integer startAddr, ArrayList<IInstruction> contents, ArrayList<Integer> locationsWrittenTo) {
        this.startAddr = startAddr;
        this.contents = contents;
        this.locationsWrittenTo = locationsWrittenTo;
    }


    public Integer execute(Stack<MemLocation> stack, Integer framePointer, Integer programCounter) {
        Integer newPC = programCounter;
        for (IInstruction instr : contents) {
            newPC = instr.execute(stack, framePointer, programCounter);
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
