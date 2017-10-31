package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.Utils;

public class NSString extends NSData<String> {
    public static final NSString EMPTY = NSString.from("");

    private NSString(String value) {
        super(Type.STRING, value);
    }

    public static NSString from(String value) {
        return new NSString(value);
    }

    @Override
    public int hashCode() {
        return getRawValue().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSString && getRawValue().equals(((NSString) o).getRawValue());
    }

    @Override
    public NSData<?> nsAdd(NSData<?> other) {
        if (other.getType() != Type.STRING) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to add non-string to string");
        }

        return NSString.from(getRawValue() + ((NSString) other).getRawValue());
    }

    @Override
    public NSNumber nsCompare(NSData<?> other) {
        if (other.getType() != Type.STRING) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to compare non-string to string");
        }
        return NSNumber.from(Utils.compare(getRawValue(), ((NSString) other).getRawValue()));
    }

    @Override
    public NSData<?> nsApplyHashOperator() {
        return NSNumber.from(getRawValue().length());
    }

    @Override
    public NSData<?> nsAccess(String member) {
        // TODO
        throw VMError.from(BuiltinClass.UnsupportedOperationError, "Invalid operation on a number");
    }

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }

    @Override
    public NSString nsToString() {
        return this;
    }
}
