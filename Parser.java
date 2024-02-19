import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import instructions.IInstruction;

public class Parser {
    private final Integer opcodeWordMask  = 0xFF000000;
    private final Integer operandWordMask = 0x00FFFFFF;
    private final Integer firstOperandBitLength = 24;
    private ArrayList<IInstruction> program = new ArrayList<IInstruction>();
    private HashMap<Integer, MemoryItem> memory = new HashMap<Integer, MemoryItem>();


    public void parse(byte[] bytes) {
        Integer dataSectionLength = getWordFromIndex(bytes, 0);
        byte[] dataSection = Arrays.copyOfRange(bytes, 4, dataSectionLength + 4);
        byte[] codeSection = Arrays.copyOfRange(bytes, dataSectionLength + 4, bytes.length);
        getDataSection(dataSection);
        getProgramSection(codeSection); 
    }


    private Integer getWordFromIndex(byte[] bytes, Integer i) {
        Integer instruction = (bytes[i] & 0xFF) << 24 | (bytes[i + 1] & 0xFF) << 16 | 
                                (bytes[i + 2] & 0xFF) << 8 | (bytes[i + 3] & 0xFF);
        return instruction;
    } 


    private void getDataSection(byte[] dataBytes) {
        String memItem = new String();
        Integer memStart = 0;
        for (int i = 0; i < dataBytes.length; i++) {
            memItem += (char)dataBytes[i];
            if (dataBytes[i] == 0) {
                memory.put(memStart, new MemoryItem(MemoryType.STRING, (Object)memItem));
                memStart = i + 1;
                memItem = new String();
            }
        }
    }


    private void getProgramSection(byte[] programBytes) {
        for (int i = 0; i < programBytes.length; i += 4) {
            Integer instruction = getWordFromIndex(programBytes, i);
            
            // get an object representing the instruction based on whether or not it is a single
            // or double word instruction
            IInstruction instructionInstance;
            switch ((instruction & opcodeWordMask) >>> firstOperandBitLength) {
                case 0x19: // double word opcode
                case 0x22:
                case 0x2C:
                    int nextInstruction = getWordFromIndex(programBytes, i + 4);
                    instructionInstance = getDoubleWordInstructionFromOpcode(instruction, nextInstruction);
                    
                    // skip next instruction in the case of a 2-word instruction
                    i += 4;
                    break;
                
                case 0xB2: // triple word opcode
                    int firstWordArg = getWordFromIndex(programBytes, i + 4);
                    int secondWordArg = getWordFromIndex(programBytes, i + 8);
                    instructionInstance = getTripleWordInstructionFromOpcode(instruction, firstWordArg, secondWordArg);
                    i += 8;
                    break;
            
                default:
                    instructionInstance = getSingleWordInstructionFromOpcode(instruction);
                    break;
            }

            this.program.add(instructionInstance);
        }
    }


    /**
     * Takes a 32-bit instruction and returns the instruction translated to an IInstruction
     * which can be executed and put into program memory.
     * @param instruction The instruction binary to convert to an IInstruction
     * @return The translated IInstruction
     * @see IInstruction
     */
    public IInstruction getSingleWordInstructionFromOpcode(int instruction) {
        int operand = instruction >>> 24;
        int argument = instruction & operandWordMask;
        switch (operand) {
            case 0x00: return new instructions.Nop();
            case 0x01: return new instructions.Add();
            case 0x02: return new instructions.Sub();
            case 0x03: return new instructions.Mult();
            case 0x04: return new instructions.Div();
            case 0x05: return new instructions.Mod();
            case 0x06: return new instructions.And();
            case 0x07: return new instructions.Or();
            case 0x08: return new instructions.Eq();
            case 0x09: return new instructions.Neq();
            case 0x0A: return new instructions.Gt();
            case 0x0B: return new instructions.Lt();
            case 0x0C: return new instructions.Gte();
            case 0x0D: return new instructions.Lte();
            case 0x0E: return new instructions.Ret();
            case 0x0F: return new instructions.Neg();
            case 0x12: return new instructions.Pushi(argument);
            case 0x13: return new instructions.Loadi(argument);
            case 0x14: return new instructions.Storei(argument);
            case 0x16: return new instructions.Jump(argument);
            case 0x17: return new instructions.JZro(argument);
            case 0x1A: return new instructions.Arri(argument);
            case 0x1B: return new instructions.Storeai();
            case 0x1E: return new instructions.Loadai();
            case 0x1F: return new instructions.CCatS();
            case 0x20: return new instructions.CCatA();
            case 0x21: return new instructions.LoadSC();
            case 0x23: return new instructions.AddF();
            case 0x24: return new instructions.SubF();
            case 0x25: return new instructions.DivF();
            case 0x26: return new instructions.MultF();
            case 0x27: return new instructions.GtF();
            case 0x28: return new instructions.GteF();
            case 0x29: return new instructions.LtF();
            case 0x2A: return new instructions.LteF();
            case 0x2B: return new instructions.ArrRng();
            case 0x2D: return new instructions.Length(); 
            case 0x2E: return new instructions.Copy();
            case 0xA0: return new instructions.Print();
            case 0xA1: return new instructions.Read();
            case 0xA2: return new instructions.Throw();
            case 0xA3: return new instructions.StrToInt();
            case 0xA4: return new instructions.IntToStr();
            case 0xB0: return new instructions.Lock();
            case 0xB1: return new instructions.Unlock();
            case 0xB3: return new instructions.EndH(argument); // end handler
            case 0xB4: return new instructions.RetH(); // return from handler
            case 0xB6: return new instructions.LoadHP(argument);
            case 0xB7: return new instructions.StartP(argument);
            case 0xB8: return new instructions.EndP(argument);
            default:
                System.err.println(String.format("Unknown single word opcode: 0x%08X", operand)); 
                return null;
        }
    }


    public IInstruction getDoubleWordInstructionFromOpcode(int firstWord, int secondWord) {
        int operand = firstWord >> firstOperandBitLength;
        int firstArgument = firstWord & operandWordMask;
        switch (operand) {
            case 0x19: return new instructions.Call(firstArgument, secondWord);
            case 0x22: return new instructions.PushF(secondWord);
            case 0x2C: return new instructions.Map(firstArgument, secondWord);
            default:
                System.err.println(String.format("Unknown double word opcode: 0x%08X", operand)); 
                return null;
        }
    }


    public IInstruction getTripleWordInstructionFromOpcode(int firstWord, int secondWord, int thirdWord) {
        int operand = firstWord >>> firstOperandBitLength;
        int firstArgument = firstWord & operandWordMask;
        switch (operand) {
            case 0xB2: return new instructions.StartH(firstArgument, secondWord, thirdWord);
            default:
                System.err.println(String.format("Unknown triple word opcode: 0x%08X", operand)); 
                return null;
        }
    }


    public HashMap<Integer, MemoryItem> getDataSection() {
        return memory;
    }


    public ArrayList<IInstruction> getProgram() {
        return program;
    }
}
