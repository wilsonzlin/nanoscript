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

    @Override
    public int hashCode() {
        return getRawValue().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSBoolean && this == o;
    }

    @Override
    public NSBoolean toNSBoolean() {
        return this;
    }

    @Override
    public NSString toNSString() {
        return NSString.from(getRawValue().toString());
    }
}
