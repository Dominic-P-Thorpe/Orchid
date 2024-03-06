import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.Map.Entry;

import instructions.Arri;
import instructions.Await;
import instructions.Call;
import instructions.EndH;
import instructions.EndP;
import instructions.IInstruction;
import instructions.LoadHP;
import instructions.Map;
import instructions.PCallSI;
import instructions.StartH;
import instructions.StartP;

/**
 * A singleton class which takes a program binary in Orchid bytecode and can execute that code
 * and display the result of running the program. Simulates the program counter, frame pointer
 * and the stack.
 */
public class VirtualMachine extends Thread {
    private Integer programCounter = 0;
    private final ArrayList<CritcalSection> criticalSections = new ArrayList<CritcalSection>();
    /** 
     * Maps functions to handle to a stack of their handlers. Functions are handled by the handler 
     * at the top of the stack, which is so that nested handlers can be implemented correctly and
     * outer handlers are ignored over inner handlers.
     */
    private final HashMap<Integer, Stack<HandlerData>> activeHandlers = new HashMap<Integer, Stack<HandlerData>>();

    /**
     * Maps memory locations which contain promises to the threads which are executing their 
     * promise. Each promise should be joined and then removed from the hashmap when a relevant
     * await occurs.
     */
    private final HashMap<Integer, VirtualMachine> promises = new HashMap<Integer, VirtualMachine>();

    private final Stack<Integer> stack = new Stack<Integer>();
    private final Stack<Integer> handlerArgsStack = new Stack<Integer>();
    private final Stack<Integer> framePointerStack = new Stack<Integer>();
    private final HashMap<Integer, MemoryItem> memory;
    private final ArrayList<IInstruction> program;
    private final Scanner scanner = new Scanner(System.in);
    
    public Integer framePointer = 0;


    /**
     * Translates the bytecode program in the byte array into IInstructions and puts them into the 
     * program memory for execution. 
     * @param programBytes The program binary this VM will run
     */
    public VirtualMachine(byte[] programBytes) {
        Parser parser = new Parser();
        parser.parse(programBytes);
        memory = parser.getDataSection();
        program = parser.getProgram();

        getCriticalSections();
    }


    @SuppressWarnings("unchecked")
    public VirtualMachine(ArrayList<IInstruction> program, Integer framePointer, Integer programCounter, 
                            HashMap<Integer, MemoryItem> memory, Stack<Integer> stack,
                            HashMap<Integer, Stack<HandlerData>> handlers) {
        this.program = program;
        this.memory = memory;
        this.framePointer = framePointer;
        this.programCounter = programCounter;

        // make sure the stack is a deep copy to prevent cross-thread modification
        for (int i = 0; i < stack.size(); i++) {
            this.stack.push(stack.get(i));
        }

        // make sure the active handlers hashmap is a deep copy, otherwise when another thread exits
        // the handler, so to shall this one.
        for (Entry<Integer, Stack<HandlerData>> entry : handlers.entrySet()) {
            this.activeHandlers.put(entry.getKey(), (Stack<HandlerData>)entry.getValue().clone());
        }
    }


    /**
     * Returns the length of the item in memory as a number of locations in memory
     * @param address The address of the item to get the length of
     * @return The length of the item as a number of memory locations (analagous to bytes)
     */
    private int getDataItemLength(int address) {
        MemoryItem data = memory.get(address);
        switch (data.memType) {
            case STRING:
                String strContents = (String)data.getContents();
                return strContents.length();
            
            case INT_ARRAY:
                int[] intArrayContents = (int[])data.getContents();
                return intArrayContents.length;
            
            case PROMISE:
                throw new PromiseAccessException(address);
        }

        return 0;
    }


    /**
     * Gets the next free address in the VM's memory
     * @return The next free address in the VM's memory
     */
    private Integer getNextMemoryAddress() {
        // if memory is empty, just return 0
        if (memory.size() == 0)
            return 0;

        // otherwise, get all the keys and sort them in descending order
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (Integer k : memory.keySet())
            keys.add(k);

        keys.sort(Collections.reverseOrder());
        Integer maxAddress = keys.get(0); // get the largest address, which is the 1st element

        // the new address is the largest address + the length of that address's contents 
        return maxAddress + getDataItemLength(maxAddress);
    }


    /**
     * If there is already an entry for the given function in the activeHandlers hashmap, then the
     * new handler with the given data will be pushed to the corresponding stack. Otherwise, a new
     * entry with a new stack will be created before pushing.
     * @param functionAddr The address of the function to handle, null if the handler is a permit
     * @param handlerAddr The address of the handler for the function, null if the handler is a permit
     * @param isPermit True if the handler is a permit, otherwise false
     */
    private void pushNewHandler(Integer functionAddr, Integer handlerAddr, Boolean isPermit) {
        HandlerData handlerData;
        if (!isPermit)
            handlerData = new HandlerData(handlerAddr, framePointer, isPermit);
        else
            handlerData = new HandlerData(null, null, isPermit);
        
        if (!activeHandlers.containsKey(functionAddr | 0xFF000000)) 
            activeHandlers.put(functionAddr | 0xFF000000, new Stack<HandlerData>());
        activeHandlers.get(functionAddr | 0xFF000000).push(handlerData);
    }


    /**
     * THIS CODE WAS COPIED FROM BAELDUNG (https://www.baeldung.com/java-concatenate-arrays),
     * accessed 2024/02/04.
     * 
     * Takes 2 arrays with the inner type T and returns an array the same as the first with the 
     * second appended.
     * @param <T> The inner type of the arrays to be concatenated, and of the resulting array
     * @param array1 The array which should be at the start of the new array
     * @param array2 The array which should be at the end of the new array
     * @return The new array: array1 + array2
     */
    private <T> T concatArrays(T array1, T array2) {
        if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
            throw new IllegalArgumentException("Only arrays are accepted.");
        }
    
        Class<?> compType1 = array1.getClass().getComponentType();
        Class<?> compType2 = array2.getClass().getComponentType();
    
        if (!compType1.equals(compType2)) {
            throw new IllegalArgumentException("Two arrays have different types.");
        }
    
        int len1 = Array.getLength(array1);
        int len2 = Array.getLength(array2);
    
        @SuppressWarnings("unchecked")
        //the cast is safe due to the previous checks
        T result = (T) Array.newInstance(compType1, len1 + len2);
    
        System.arraycopy(array1, 0, result, 0, len1);
        System.arraycopy(array2, 0, result, len1, len2);
    
        return result;
    }


    /**
     * Executes the current program in the program memory. Will terminate when the program
     * counter's value is outside the range 0 - 0x00FFFFFE, which is the signal to halt the
     * execution. Prints the stack after execution.
     */
    public void execute() {
        while (programCounter >= 0 && programCounter < 0x00FFFFFF) {
            Integer newFramePointer = framePointer;
            

            IInstruction instruction;
            try {
                instruction = this.program.get(programCounter);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            // System.out.println(String.format("0x%08X: ", programCounter) + instruction);
            programCounter = instruction.execute(stack, framePointer, programCounter);
            if (instruction instanceof instructions.Call) {
                Call callInstr = (instructions.Call)instruction;
                Integer numArguments = callInstr.paramCount;
                newFramePointer = stack.size() - 2 - numArguments;
            }

            else if (instruction instanceof instructions.Ret) {
                newFramePointer = stack.remove(stack.size() - 2); // remove and return 2nd from top elem on stack
            }

            else if (instruction instanceof instructions.Arri) {
                Arri arrI = (Arri)instruction;
                Integer address = getNextMemoryAddress();
                memory.put(address, new MemoryItem(MemoryType.INT_ARRAY, new int[arrI.length]));
                stack.push(address);
            } else if (instruction instanceof instructions.Storeai) {
                Integer index = stack.pop();
                Integer value = stack.pop();
                int[] array = (int[])memory.get(stack.peek()).getContents();
                array[index] = value;
                memory.get(stack.peek()).setContents(array);
            } else if (instruction instanceof instructions.Loadai) {
                Integer index = stack.pop();
                Integer address = stack.pop();
                int[] array = (int[])memory.get(address).getContents();
                stack.push(array[index]);
            } else if (instruction instanceof instructions.LoadSC) {
                Integer index = stack.pop();
                Integer address = stack.pop();
                String string = (String)memory.get(address).getContents();
                stack.push((int)string.charAt(index));
            }

            else if (instruction instanceof instructions.CCatS) {
                String postfix = (String)memory.get(stack.pop()).getContents();
                String prefix = (String)memory.get(stack.pop()).getContents();
                Integer address = getNextMemoryAddress();
                memory.put(address, new MemoryItem(MemoryType.STRING, prefix + postfix));
                stack.push(address);
            } else if (instruction instanceof instructions.CCatA) {
                int[] postfix = (int[])memory.get(stack.pop()).getContents();
                int[] prefix = (int[])memory.get(stack.pop()).getContents();
                int[] concatenated = concatArrays(prefix, postfix);

                Integer address = getNextMemoryAddress();
                memory.put(address, new MemoryItem(MemoryType.INT_ARRAY, (Object)concatenated));
                stack.push(address);
            }

            else if (instruction instanceof instructions.StartH) {
                StartH startH = (StartH)instruction;
                pushNewHandler(startH.funcToHandleAddr, startH.handlerAddr, false);
            } else if (instruction instanceof instructions.EndH) {
                EndH endH = (EndH)instruction;
                activeHandlers.get(endH.funcToHandleAddr | 0xFF000000).pop();
            } else if (instruction instanceof instructions.StartP) {
                StartP startP = (StartP)instruction;
                pushNewHandler(startP.funcToHandleAddr, null, true);
            } else if (instruction instanceof instructions.EndP) {
                EndP endP = (EndP)instruction;
                activeHandlers.get(endP.funcToHandleAddr | 0xFF000000).pop();
            }

            else if (instruction instanceof instructions.Print) {
                // throw a RuntimeError if this function is unhandled
                Stack<HandlerData> handlerStack;
                if (activeHandlers.containsKey(0xFFFFFFF0))
                    handlerStack = activeHandlers.get(0xFFFFFFF0);
                else
                    throw new RuntimeException("Print function unhandled!");

                // execute this branch if the function has a handler (not a permit)
                if (!handlerStack.peek().isPermit) {                   
                    handlerArgsStack.push(stack.pop()); // transfer ordering arg to handler args stack
                    handlerArgsStack.push(stack.pop()); // transfer msg ptr arg to handler args stack
                    stack.push(programCounter); // push a pseudo return pointer
                    programCounter = activeHandlers.get(0xFFFFFFF0).peek().handlerAddr / 4;

                    // during the handler, restore the frame pointer back to where it should be
                    // for the function containing the handler
                    framePointerStack.push(framePointer);
                    newFramePointer = activeHandlers.get(0xFFFFFFF0).peek().framePtr;
                } else { // if there is a permit for this effect, run it normally
                    stack.pop(); // dont care about the ordering param on top of the stack
                    String msg = (String)memory.get(stack.pop()).getContents();
                    System.out.println(msg);
                }
            }

            else if (instruction instanceof instructions.Read) {
                // execute this branch if the function has a handler (not a permit)
                if (activeHandlers.containsKey(0xFFFFFFF1) && !activeHandlers.get(0xFFFFFFF1).peek().isPermit) {
                    stack.push(programCounter); // push a pseudo return pointer
                    programCounter = activeHandlers.get(0xFFFFFFF1).peek().handlerAddr / 4;

                    // during the handler, restore the frame pointer back to where it should be
                    // for the function containing the handler
                    framePointerStack.push(framePointer);
                    newFramePointer = activeHandlers.get(0xFFFFFFF0).peek().framePtr;
                } else { // if there is a permit for this effect, run it normally
                    String input = scanner.nextLine();
                    Integer address = getNextMemoryAddress();
                    memory.put(address, new MemoryItem(MemoryType.STRING, input));
                    stack.push(address);
                }
            }

            else if (instruction instanceof instructions.Throw) {
                // execute this branch if the function has a handler (not a permit)
                if (activeHandlers.containsKey(0xFFFFFFF2) && !activeHandlers.get(0xFFFFFFF2).peek().isPermit) {
                    handlerArgsStack.push(stack.pop()); // transfer msg ptr arg to handler args stack
                    stack.push(programCounter); // push a pseudo return pointer
                    programCounter = activeHandlers.get(0xFFFFFFF2).peek().handlerAddr / 4;

                    // during the handler, restore the frame pointer back to where it should be for
                    // the function containing the handler
                    framePointerStack.push(framePointer);
                    newFramePointer = activeHandlers.get(0xFFFFFFF2).peek().framePtr;
                } else { // if there is a permit for this effect, run it normally
                    throw new RuntimeException(new Exception((String)memory.get(stack.pop()).getContents()));
                }
            }

            else if (instruction instanceof instructions.RetH) {
                newFramePointer = framePointerStack.pop();
            }

            else if (instruction instanceof instructions.LoadHP) {
                LoadHP loadHP = (LoadHP)instruction;
                stack.set(framePointer + loadHP.address / 4, handlerArgsStack.pop());
            }

            else if (instruction instanceof instructions.ArrRng) {
                int end = stack.pop();
                int start = stack.pop();
                int[] arrayToStore = IntStream.range(start, end).toArray();

                Integer address = getNextMemoryAddress();
                memory.put(address, new MemoryItem(MemoryType.INT_ARRAY, (Object)arrayToStore));
                stack.push(address);
            } else if (instruction instanceof instructions.Map) {
                Integer address = getNextMemoryAddress(); // address for the new array

                Map mapInstr = (Map)instruction;
                int[] array = (int[])memory.get(stack.pop()).getContents();

                // create an array of new virtual machines to run the map code in parallel.
                VirtualMachine[] vms = new VirtualMachine[array.length];
                int[] newArray = new int[array.length];
                for (int i = 0; i < array.length; i++) {
                    vms[i] = new VirtualMachine(program, 0, mapInstr.targetAddr / 4, memory, stack, activeHandlers);
                    vms[i].push(0); // push starting frame pointer, which is 0
                    vms[i].push(-1); // push return address which will terminate execution

                    // add the parameter to the stack
                    vms[i].push(array[i]);
                    vms[i].run();
                }

                // synchronise all the parallel threads running the map code and insert their
                // results into the new array
                for (int i = 0; i < array.length; i++) {
                    try {
                        vms[i].join();
                        newArray[i] = vms[i].pop();
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                        newArray[i] = -1;
                    }
                }

                // add the new array to memory
                memory.put(address, new MemoryItem(MemoryType.INT_ARRAY, (Object)newArray));
                stack.push(address);
            } else if (instruction instanceof instructions.PCallSI) {
                PCallSI pcallsiInstr = (PCallSI)instruction;

                // create a VM to run the relevant code block in parallel
                VirtualMachine vm = new VirtualMachine(program, 0, pcallsiInstr.funcStartAddr / 4, memory, stack, activeHandlers);
                vm.push(0); // push starting frame pointer, which is 0
                vm.push(-1); // push return address which will terminate execution

                // pop the function arguments off this VM's stack and onto the new VM's stack
                Stack<Integer> argsStack = new Stack<Integer>();
                for (int index = 0; index < pcallsiInstr.numArgs; index++) {
                    argsStack.push(stack.pop());
                }

                for (int index = 0; index < pcallsiInstr.numArgs; index++) {
                    vm.push(argsStack.pop());
                }

                promises.put(pcallsiInstr.destinationAddr, vm);
                vm.start();
            } else if (instruction instanceof instructions.Await) {
                Await awaitInstr = (Await)instruction;
                if (promises.get(awaitInstr.addr) != null) {
                    VirtualMachine vmThread = promises.get(awaitInstr.addr);
                    if (vmThread == null) // skip if the promise has already been awaited
                        continue;

                    // otherwise, join the thread to sync it with the current thread
                    try {
                        vmThread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    
                    stack.set(awaitInstr.addr / 4 + framePointer, vmThread.pop());
                    promises.remove(awaitInstr.addr);
                }
            }
            
            else if (instruction instanceof instructions.Length) {
                int[] array = (int[])memory.get(stack.pop()).getContents();
                stack.push(array.length);
            }

            else if (instruction instanceof instructions.StrToInt) {
                String string = (String)memory.get(stack.pop()).getContents();
                // remember to remove the null byte at the end of the string
                stack.push(Integer.parseInt(string.substring(0, string.length() - 1)));
            } else if (instruction instanceof instructions.IntToStr) {
                String string = stack.pop().toString();
                Integer address = getNextMemoryAddress();
                
                memory.put(address, new MemoryItem(MemoryType.STRING, string));
                stack.push(address);
            }

            framePointer = newFramePointer;
        }
    
        printStack();
    }


    public void run() {
        this.execute();
    }


    public Integer pop() {
        return this.stack.pop();
    }


    public void push(Integer item) {
        this.stack.push(item);
    }


    public Integer peek() {
        return this.stack.peek();
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
        System.out.println("----STACK----");
        stack.forEach(System.out::println);
        System.out.println("-------------");
    }
}
