import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Orchid {
    public static void main(String[] args) {
        String filename = args[0];
        System.out.println("Executing: " + filename);
        Path path = Paths.get(filename);
        try {
            byte[] data = Files.readAllBytes(path);
            VirtualMachine vm = VirtualMachine.getInstance(data);
            vm.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}