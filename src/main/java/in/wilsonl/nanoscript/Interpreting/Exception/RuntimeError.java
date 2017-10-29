package in.wilsonl.nanoscript.Interpreting.Exception;

public abstract class RuntimeError extends RuntimeException {
    public RuntimeError(String message) {
        super(message);
    }
}
