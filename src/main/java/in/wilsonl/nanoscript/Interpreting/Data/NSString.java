package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalError;
import in.wilsonl.nanoscript.Syntax.Operator;

public class NSString extends NSData<String> {
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
                throw new InternalError("Invalid relation operator on string");
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
    public NSData<?> applyBinaryOperator(Operator operator, NSData<?> other) {
        if (other.getType() != Type.STRING) {
            throw new UnsupportedOperationException("Attempted to operate non-string to string");
        }

        String thisString = getRawValue();
        String otherString = (String) other.getRawValue();

        switch (operator) {
            case PLUS:
                return NSString.from(thisString + otherString);

            case EQ:
            case NEQ:
            case LT:
            case LEQ:
            case GT:
            case GEQ:
                return NSBoolean.from(applyRelation(thisString, operator, otherString));

            case SPACESHIP:
                return NSNumber.from(thisString.compareTo(otherString));

            default:
                throw new UnsupportedOperationException("Invalid operation on a string");
        }
    }

    @Override
    public NSData<?> applyUnaryOperator(Operator operator) {
        switch (operator) {
            case HASH:
                return NSNumber.from(getRawValue().length());

            default:
                throw new UnsupportedOperationException("Invalid operation on a string");
        }
    }

    @Override
    public NSData<?> applyAccess(String member) {
        // TODO
        throw new UnsupportedOperationException("Invalid operation on a number");
    }

    @Override
    public NSString toNSString() {
        return this;
    }
}
