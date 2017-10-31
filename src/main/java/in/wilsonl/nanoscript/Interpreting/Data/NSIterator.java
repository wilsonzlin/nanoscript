package in.wilsonl.nanoscript.Interpreting.Data;

public abstract class NSIterator extends NSData<Object> {
    protected NSIterator() {
        super(Type.ITERATOR, null);
    }

    public abstract NSData<?> next();

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }
}
