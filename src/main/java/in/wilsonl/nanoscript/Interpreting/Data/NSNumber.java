package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.Operator;

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

    private static double applyArithmetic(double o1, Operator operator, double o2) {
        double result;
        switch (operator) {
            case PLUS:
                result = o1 + o2;
                break;

            case MINUS:
                result = o1 - o2;
                break;

            case MULTIPLY:
                result = o1 * o2;
                break;

            case DIVIDE:
                result = o1 / o2;
                break;

            case EXPONENTIATE:
                result = Math.pow(o1, o2);
                break;

            case MODULO:
                result = o1 % o2;
                break;

            default:
                throw new InternalStateError("Invalid arithmetic operator on number");
        }

        return result;
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
    public NSBoolean nsTestEquality(NSData<?> other) {
        return NSBoolean.from(nsCompare(other).getRawValue() == 0);
    }

    @Override
    public NSNumber nsCompare(NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.UnsupportedOperationError, "Attempted to compare non-number to number");
        }

        double thisNumber = getRawValue();
        double otherNumber = (Double) other.getRawValue();

        return NSNumber.from(Double.compare(thisNumber, otherNumber));
    }

    @Override
    public NSData<?> nsApplyBinaryOperator(Operator operator, NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.UnsupportedOperationError, "Attempted to operate non-number to number");
        }

        double thisNumber = getRawValue();
        double otherNumber = (Double) other.getRawValue();

        switch (operator) {
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case EXPONENTIATE:
            case MODULO:
                return NSNumber.from(applyArithmetic(thisNumber, operator, otherNumber));

            default:
                throw VMError.from(BuiltinClass.UnsupportedOperationError, "Invalid operation on a number");
        }
    }

    @Override
    public NSData<?> nsAccess(String member) {
        // TODO
        throw VMError.from(BuiltinClass.UnsupportedOperationError, "Invalid operation on a number");
    }

    @Override
    public NSString nsToString() {
        Double rawValue = getRawValue();
        if (rawValue == Math.floor(rawValue) && !Double.isInfinite(rawValue)) {
            return NSString.from(String.valueOf(toInt()));
        }
        return NSString.from(String.valueOf(rawValue));
    }
}
