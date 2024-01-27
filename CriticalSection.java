class CritcalSection {
    public final Integer startAddress;
    public final Integer endAddress;
    private Integer ownerPid = null;
    private Boolean locked = false;


    public CritcalSection(Integer start, Integer end) {
        startAddress = start;
        endAddress = end;
    }


    public Boolean tryAcquire(Integer pid) {
        if (locked)
            return false;

        ownerPid = pid;
        return true;
    }


    public void release(Integer pid) {
        if (pid == ownerPid)
            locked = false;
    }
}