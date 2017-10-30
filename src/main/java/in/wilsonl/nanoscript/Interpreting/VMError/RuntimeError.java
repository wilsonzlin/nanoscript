package in.wilsonl.nanoscript.Interpreting.VMError;

public abstract class RuntimeError extends RuntimeException {
    public RuntimeError(String message) {
        super(message);
    }
}
