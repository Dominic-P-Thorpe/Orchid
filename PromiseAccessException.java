public class PromiseAccessException extends RuntimeException {
    public PromiseAccessException(Integer address) {
        super("The value at memory location " + address + " is a promise, did you mean to preceed this with an await?");
    }
}
