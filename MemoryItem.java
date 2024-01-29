public class MemoryItem {
    private Object contents;
    public final MemoryType memType;


    public MemoryItem(MemoryType memType, Object contents) {
        this.contents = contents;
        this.memType = memType;
    }


    public Object getContents() {
        return contents;
    }


    public void setContents(Object contents) {
        this.contents = contents;
    }
}
