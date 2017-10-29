package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Syntax.Operator;

public class NSNumber extends NSData<Double> {
    private NSNumber(Double value) {
        super(Type.NUMBER, value);
    }

    public static NSNumber from(double value) {
        return new NSNumber(value);
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

    private static boolean applyRelation(double o1, Operator operator, double o2) {
        int compare = Double.compare(o1, o2);
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
                throw new InternalStateError("Invalid relation operator on number");
        }
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
    public NSData<?> applyBinaryOperator(Operator operator, NSData<?> other) {
        if (other.getType() != Type.NUMBER) {
            throw new UnsupportedOperationException("Attempted to operate non-number to number");
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

            case EQ:
            case NEQ:
            case LT:
            case LEQ:
            case GT:
            case GEQ:
                return NSBoolean.from(applyRelation(thisNumber, operator, otherNumber));

            case SPACESHIP:
                return NSNumber.from(Double.compare(thisNumber, otherNumber));

            default:
                throw new UnsupportedOperationException("Invalid operation on a number");
        }
    }

    @Override
    public NSData<?> applyAccess(String member) {
        // TODO
    }

    @Override
    public NSBoolean toNSBoolean() {
        throw new UnsupportedOperationException("Invalid operation on a number");
    }
}
