package in.wilsonl.nanoscript.Interpreting.VMError;

public class EndOfIterationError extends RuntimeError {
    public EndOfIterationError() {
        super("Iterator has no more values");
    }
}
