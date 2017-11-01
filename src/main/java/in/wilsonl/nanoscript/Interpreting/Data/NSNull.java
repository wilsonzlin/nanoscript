package in.wilsonl.nanoscript.Interpreting.Data;

public class NSNull extends NSData {
    public static final NSNull NULL = new NSNull();

    private NSNull() {
        super(Type.NULL);
    }

    // Don't need hashCode or equals, as the default implements are correct as long
    // as only <NULL> is used

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.FALSE;
    }
}
