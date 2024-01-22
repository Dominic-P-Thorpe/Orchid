import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

class Orchid {
    private static BasicBlock getBasicBlockFromEntrypoint(ArrayList<BasicBlock> blocks, Integer target) {
        for (BasicBlock basicBlock : blocks) {
            if (basicBlock.getStartAddr() == target)
                return basicBlock;
        }

        return null;
    }


    public static void main(String[] args) {
        String filename = args[0];
        System.out.println("Executing: " + filename);
        Path path = Paths.get(filename);
        try {
            byte[] data = Files.readAllBytes(path);
            BasicBlockFactory basicBlockFactory = new BasicBlockFactory();
            ArrayList<BasicBlock> blocks = basicBlockFactory.getBasicBlock(data, new ArrayList<BasicBlock>());
            ArrayList<BasicBlock> assembledBlocks = basicBlockFactory.assembleBasicBlocks(blocks);
            for (BasicBlock block : assembledBlocks) {
                block.printBlock();
                System.out.println("\n");
            }

            Stack<instructions.MemLocation> stack = new Stack<instructions.MemLocation>();

            Integer PC = 0;
            BasicBlock nextBlock = assembledBlocks.get(PC);
            while (PC > -1 && PC < 0xFFFFFF) {
                try {
                    PC = nextBlock.execute(stack, 0, PC);
                    nextBlock = getBasicBlockFromEntrypoint(assembledBlocks, PC);
                } catch (NullPointerException e) {
                    break;
                }
            }

            System.out.println("---------------");
            stack.stream().forEach(a -> System.out.println(a.read()));
            System.out.println("---------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}