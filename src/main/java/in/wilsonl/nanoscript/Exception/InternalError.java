package in.wilsonl.nanoscript.Exception;

public class InternalError extends Error {
    public InternalError(String message) {
        super(String.format("\n==============================================\nPLEASE REPORT THIS ERROR:\n\n%s\n\nThis is an internal error that should not have happened. Please report this error, along with its stack trace, to ooml-lang developers.", message));
    }
}
