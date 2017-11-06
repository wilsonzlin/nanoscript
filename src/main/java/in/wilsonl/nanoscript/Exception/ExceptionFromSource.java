package in.wilsonl.nanoscript.Exception;

public class ExceptionFromSource extends RuntimeException {
    public ExceptionFromSource(String source, Exception e) {
        super(String.format("[%s] %s", source, e));
    }
}
