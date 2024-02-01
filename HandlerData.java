public class HandlerData {
    public final Integer handlerAddr;
    public final Integer framePtr;


    public HandlerData(Integer handlerAddr, Integer framePtr) {
        this.handlerAddr = handlerAddr;
        this.framePtr = framePtr;
    }
}
