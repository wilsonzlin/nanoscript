package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Syntax.Expression.CallExpression;
import in.wilsonl.nanoscript.Syntax.Expression.LookupExpression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Operator;

import java.util.List;

public abstract class NSData<T> {
    private final Type type;
    private final T rawValue;

    protected NSData(Type type, T rawValue) {
        this.type = type;
        this.rawValue = rawValue;
    }

    public Type getType() {
        return type;
    }

    public T getRawValue() {
        return rawValue;
    }

    public NSData<?> applyUnaryOperator(Operator operator) {
        throw new UnsupportedOperationException(String.format("The operator %s cannot be applied to %s", operator, type));
    }

    public NSData<?> applyBinaryOperator(Operator operator, NSData<?> other) {
        throw new UnsupportedOperationException(String.format("The operator %s cannot be applied to %s", operator, type));
    }

    public NSData<?> applyCall(List<NSData<?>> arguments) {
        throw new UnsupportedOperationException(String.format("%s cannot be called", type));
    }

    public NSData<?> applyLookup(List<NSData<?>> terms) {
        throw new UnsupportedOperationException(String.format("%s cannot be looked up", type));
    };

    public NSData<?> applyUpdate(List<NSData<?>> terms, NSData<?> value) {
        throw new UnsupportedOperationException(String.format("%s cannot be updated", type));
    }

    public NSData<?> applyAccess(String member) {
        throw new UnsupportedOperationException(String.format("%s does not have any members", type));
    }

    public NSData<?> applyAssignment(String member, NSData<?> value) {
        throw new UnsupportedOperationException(String.format("%s does not have any members", type));
    }

    public NSBoolean toNSBoolean() {
        throw new UnsupportedOperationException(String.format("%s cannot be converted to a boolean", type));
    }

    public NSString toNSString() {
        throw new UnsupportedOperationException(String.format("%s cannot be stringified", type));
    }

    public enum Type {
        BOOLEAN, NULL, NUMBER, STRING, CALLABLE, OBJECT, CLASS
    }
}
