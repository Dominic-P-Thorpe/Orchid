import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

class Orchid {
    public static void main(String[] args) {
        String filename = args[0];
        System.out.println("Executing: " + filename);
        Path path = Paths.get(filename);
        try {
            byte[] data = Files.readAllBytes(path);
            // VirtualMachine vm = VirtualMachine.getInstance(data);
            // vm.execute();
            BasicBlockFactory basicBlockFactory = new BasicBlockFactory();
            ArrayList<BasicBlock> blocks = basicBlockFactory.getBasicBlock(data, new ArrayList<BasicBlock>());
            ArrayList<BasicBlock> assembledBlocks = basicBlockFactory.assembleBasicBlocks(blocks);
            for (BasicBlock block : assembledBlocks) {
                block.printBlock();
                System.out.println("\n");
            }

            assembledBlocks.get(0).execute(new Stack<instructions.MemLocation>(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}