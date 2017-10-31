package in.wilsonl.nanoscript.Exception;

public class InternalStateError extends Error {
    public InternalStateError(String message) {
        super(String.format("\n==============================================\nPLEASE REPORT THIS ERROR:\n\n%s\n\nThis is an internal error that should not have happened. Please report this error, along with its stack trace, to nanoscript developers.", message));
    }
}
