package in.wilsonl.nanoscript.Interpreting.Data;

public class NSBoolean extends NSData<Boolean> {
    public static final NSBoolean TRUE = new NSBoolean(true);
    public static final NSBoolean FALSE = new NSBoolean(false);

    private NSBoolean(boolean value) {
        super(Type.BOOLEAN, value);
    }

    public static NSBoolean from(boolean b) {
        return b ? TRUE : FALSE;
    }

    public NSBoolean invert() {
        return from(!getRawValue());
    }

    // Don't need hashCode or equals, as the default implements are correct as long
    // as only <TRUE> and <FALSE> are used

    @Override
    public NSBoolean nsToBoolean() {
        return this;
    }

    @Override
    public NSString nsToString() {
        return NSString.from(getRawValue().toString());
    }
}
