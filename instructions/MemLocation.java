package instructions;
public class MemLocation {
    private boolean hasBeenWritten = false;
    private Integer value;
    

    public MemLocation(Integer value) {
        this.value = value;
    }


    public void write(Integer value) {
        this.value = value;
        hasBeenWritten = true;
    }


    public Integer read() {
        return this.value;
    }


    public boolean hasBeenWritten() {
        return hasBeenWritten;
    }
}
