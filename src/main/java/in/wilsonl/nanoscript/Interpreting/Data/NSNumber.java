package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;

import static in.wilsonl.nanoscript.Utils.Utils.compare;
import static in.wilsonl.nanoscript.Utils.Utils.isInt;

public class NSNumber extends NSData<Double> {
    private NSNumber(Double value) {
        super(Type.NUMBER, value);
    }

    public static NSNumber from(double value) {
        return new NSNumber(value);
    }

    public static NSNumber from(Number value) {
        return new NSNumber(value.doubleValue());
    }

    public int toInt() {
        return getRawValue().intValue();
    }

    @Override
    public int hashCode() {
        return getRawValue().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSNumber && getRawValue().equals(((NSNumber) o).getRawValue());
    }

    @Override
    public NSNumber nsCompare(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to compare non-number to number");
        }

        double thisNumber = getRawValue();
        double otherNumber = (Double) other.getRawValue();

        return NSNumber.from(compare(thisNumber, otherNumber));
    }

    @Override
    public NSData<?> nsAdd(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to add non-number to number");
        }

        return NSNumber.from(getRawValue() + ((NSNumber) other).getRawValue());
    }

    @Override
    public NSData<?> nsSubtract(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to subtract non-number to number");
        }

        return NSNumber.from(getRawValue() - ((NSNumber) other).getRawValue());
    }

    @Override
    public NSData<?> nsMultiply(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to multiply non-number to number");
        }

        return NSNumber.from(getRawValue() * ((NSNumber) other).getRawValue());
    }

    @Override
    public NSData<?> nsDivide(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to divide non-number to number");
        }

        return NSNumber.from(getRawValue() / ((NSNumber) other).getRawValue());
    }

    @Override
    public NSData<?> nsExponentiate(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to exponentiate non-number to number");
        }

        return NSNumber.from(Math.pow(getRawValue(), ((NSNumber) other).getRawValue()));
    }

    @Override
    public NSData<?> nsModulo(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.TypeError, "Attempted to modulo non-number to number");
        }

        return NSNumber.from(getRawValue() % ((NSNumber) other).getRawValue());
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
        Double rawValue = getRawValue();
        if (isInt(rawValue)) {
            return NSString.from(String.valueOf(toInt()));
        }
        return NSString.from(String.valueOf(rawValue));
    }
}
