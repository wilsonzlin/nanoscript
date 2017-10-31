package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Utils.Utils;

public class NSString extends NSData<String> {
    public static final NSString EMPTY = NSString.from("");

    private NSString(String value) {
        super(Type.STRING, value);
    }

    public static NSString from(String value) {
        return new NSString(value);
    }

    private static boolean applyRelation(String o1, Operator operator, String o2) {
        int compare = o1.compareTo(o2);
        switch (operator) {
            case EQ:
                return compare == 0;
            case NEQ:
                return compare != 0;
            case LT:
                return compare < 0;
            case LEQ:
                return compare <= 0;
            case GT:
                return compare > 0;
            case GEQ:
                return compare >= 0;
            default:
                throw new InternalStateError("Invalid relation operator on string");
        }
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
            throw VMError.from(BuiltinClass.UnsupportedOperationError, "Attempted to add non-string to string");
        }

        return NSString.from(getRawValue() + ((NSString) other).getRawValue());
    }

    @Override
    public NSNumber nsCompare(NSData<?> other) {
        if (other.getType() != Type.STRING) {
            throw VMError.from(BuiltinClass.UnsupportedOperationError, "Attempted to compare non-string to string");
        }
        return NSNumber.from(Utils.compare(getRawValue(), ((NSString) other).getRawValue()));
    }

    @Override
    public NSData<?> nsApplyHashOperator(NSData<?> other) {
        return NSNumber.from(getRawValue().length());
    }

    @Override
    public NSData<?> nsAccess(String member) {
        // TODO
        throw VMError.from(BuiltinClass.UnsupportedOperationError, "Invalid operation on a number");
    }

    @Override
    public NSString nsToString() {
        return this;
    }
}
