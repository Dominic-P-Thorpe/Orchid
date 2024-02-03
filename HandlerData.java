public class HandlerData {
    /** True if the handler is a permit handler, otherwise false */
    public final Boolean isPermit;
    /** The address of the handler to invoke when the function to handle is invoked, null if the 
     * handler is a permit */
    public final Integer handlerAddr;
    /** The frame pointer to go to when the handler is invoked, null if the handler is a permit */
    public final Integer framePtr;


    /**
     * Creates a new instance of handler data which contains information about a potentially active
     * effect handler in the current context of the virtual machine. 
     * @param handlerAddr The address of the handler to invoke when the function to handle is
     * invoked, null if the handler is a permit
     * @param framePtr The frame pointer to go to when the handler is invoked, null if the handler
     * is a permit
     * @param isPermit True if the handler is a permit, otherwise false
     */
    public HandlerData(Integer handlerAddr, Integer framePtr, Boolean isPermit) {
        this.handlerAddr = handlerAddr;
        this.framePtr = framePtr;
        this.isPermit = isPermit;
    }
}
