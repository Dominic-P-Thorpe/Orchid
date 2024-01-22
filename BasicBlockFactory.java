import java.util.ArrayList;

import instructions.IInstruction;
import instructions.IJump;
import instructions.Ret;

public class BasicBlockFactory {
    public ArrayList<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();


    public ArrayList<BasicBlock> getBasicBlock(byte[] programBytes, ArrayList<BasicBlock> entry) {
        ArrayList<IInstruction> instructionsInBlock = new ArrayList<IInstruction>();
        ArrayList<Integer> locationsWrittenTo = new ArrayList<Integer>();

        int instructionIndex = 0;
        for (int i = 0; i < programBytes.length; i += 4) {
            int instructionBinary = (programBytes[i] & 0xFF) << 24 | (programBytes[i + 1] & 0xFF) << 16 | 
                            (programBytes[i + 2] & 0xFF) << 8 | (programBytes[i + 3] & 0xFF);
            
            IInstruction instruction = getInstructionFromOpcode(instructionBinary);
            if (instruction instanceof instructions.Nop || instruction instanceof instructions.IJump) {
                ArrayList<IInstruction>instructionsClone = new ArrayList<IInstruction>(); 

                // clone the old instructions in the block
                for (IInstruction instr : instructionsInBlock) 
                    instructionsClone.add(instr);

                // add the new instruction if not a NOP as that is a special case
                if (!(instruction instanceof instructions.Nop))
                    instructionsClone.add(instruction);

                if (instructionsClone.size() != 0) {
                    BasicBlock newBlock = new BasicBlock(instructionIndex, instructionsClone, locationsWrittenTo);
                    basicBlocks.add(newBlock);
                }

                instructionIndex += instructionsClone.size();
                instructionsInBlock.clear();

                if (instruction instanceof instructions.Nop)
                    instructionsInBlock.add(instruction);
            } 

            else
                instructionsInBlock.add(instruction);
        }

        // make sure that the final basic block is added as well
        BasicBlock newBlock = new BasicBlock(instructionIndex, instructionsInBlock, locationsWrittenTo);
        basicBlocks.add(newBlock);

        return basicBlocks;
    }


    private BasicBlock getBasicBlockFromEntrypoint(ArrayList<BasicBlock> blocks, Integer target) {
        for (BasicBlock basicBlock : blocks) {
            if (basicBlock.getStartAddr() == target / 4) 
                return basicBlock;
        }

        return null;
    }


    public ArrayList<BasicBlock> assembleBasicBlocks(ArrayList<BasicBlock> blocks) {
        if (blocks.size() == 0)
            return blocks;
        
        for (int i = 0; i < blocks.size(); i++) {
            IInstruction lastInstr = blocks.get(i).getContents().get(blocks.get(i).getContents().size() - 1);

            // if we have a jump, add an exit point to the basic block where that jump goes to
            if (lastInstr instanceof IJump) {
                IJump jumpInstr = (IJump)lastInstr;
                Integer jumpTargetAddr = jumpInstr.getArgument();
                BasicBlock jumpToBasicBlock = getBasicBlockFromEntrypoint(blocks, jumpTargetAddr);
                try {
                    jumpToBasicBlock.addEntrypoint(blocks.get(i));
                    blocks.get(i).addExitpoint(jumpToBasicBlock);
                } catch (NullPointerException e) {}
            } 

            else if (lastInstr instanceof Ret) {
                return blocks;
            }

            // all blocks should have an exit point to the next block sequentially in the code
            // unless there is an unconditional jump
            if (!(lastInstr instanceof instructions.Jump) && i + 1 < blocks.size()) {
                blocks.get(i).addExitpoint(blocks.get(i + 1));
                blocks.get(i + 1).addEntrypoint(blocks.get(i));
            }
        }
        
        return blocks;
    }


    /**
     * Takes a 32-bit instruction and returns the instruction translated to an IInstruction
     * which can be executed and put into program memory.
     * @param instruction The instruction binary to convert to an IInstruction
     * @return The translated IInstruction
     * @see IInstruction
     */
    private IInstruction getInstructionFromOpcode(int instruction) {
        int operand = instruction >> 24;
        int argument = instruction & 0x00FFFFFF;
        switch (operand) {
            case 0x00: return new instructions.Nop();
            case 0x01: return new instructions.Add();
            case 0x02: return new instructions.Sub();
            case 0x03: return new instructions.Mult();
            case 0x04: return new instructions.Div();
            case 0x0A: return new instructions.Gt();
            case 0x0B: return new instructions.Lt();
            case 0x0E: return new instructions.Ret();
            case 0x12: return new instructions.Pushi(argument);
            case 0x13: return new instructions.Loadi(argument);
            case 0x14: return new instructions.Storei(argument);
            case 0x16: return new instructions.Jump(argument);
            case 0x17: return new instructions.JZro(argument);
            case 0x18: return new instructions.Jnzro(argument);
            case 0x1E: return new instructions.PhiNode();
            default:   return null;
        }
    }
}
