package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Syntax.Operator;

public class NSNull extends NSData<Object> {
    public static final NSNull NULL = new NSNull();

    private NSNull() {
        super(Type.NULL, null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSNull && o == NULL;
    }

    @Override
    public NSData<?> applyBinaryOperator(Operator operator, NSData<?> other) {
        switch (operator) {
            case EQ:
                return NSBoolean.from(equals(other));

            case NEQ:
                return NSBoolean.from(!equals(other));

            default:
                throw new UnsupportedOperationException("Invalid operation on null");
        }
    }

    @Override
    public NSBoolean toNSBoolean() {
        return NSBoolean.FALSE;
    }
}
