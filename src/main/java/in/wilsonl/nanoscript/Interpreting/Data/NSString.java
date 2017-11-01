package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.Utils;

// REMEMBER: Strings are immutable; return a new one when changing
public class NSString extends NSData {
    public static final NSString EMPTY = NSString.from("");
    private final String rawString;
    private final NSNumber length;

    private NSString(String value) {
        super(Type.STRING);
        rawString = value;
        length = NSNumber.from(value.length());
    }

    public static NSString from(String value) {
        return new NSString(value);
    }

    public String getRawString() {
        return rawString;
    }

    @Override
    public int hashCode() {
        return rawString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSString && rawString.equals(((NSString) o).rawString);
    }

    @Override
    public NSData nsAdd(NSData other) {
        if (other.getType() != Type.STRING) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to add non-string to string");
        }

        return NSString.from(rawString + ((NSString) other).rawString);
    }

    @Override
    public NSNumber nsCompare(NSData other) {
        if (other.getType() != Type.STRING) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to compare non-string to string");
        }
        return NSNumber.from(Utils.compare(rawString, ((NSString) other).rawString));
    }

    @Override
    public NSData nsApplyHashOperator() {
        return length;
    }

    @Override
    public NSData nsAccess(String member) {
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
