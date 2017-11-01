package in.wilsonl.nanoscript.Interpreting.Data;

public abstract class NSIterator extends NSData {
    protected NSIterator() {
        super(Type.ITERATOR);
    }

    public abstract NSData next();

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }
}
