package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Evaluator.EvaluationResult;
import in.wilsonl.nanoscript.Interpreting.VMError.UnsupportedOperationError;
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

    public NSIterator iterate() {
        throw new UnsupportedOperationError(String.format("%s values cannot be iterated", type));
    }

    public NSData<?> applyUnaryOperator(Operator operator) {
        throw new UnsupportedOperationError(String.format("The operator %s cannot be applied to %s values", operator, type));
    }

    public NSData<?> applyBinaryOperator(Operator operator, NSData<?> other) {
        throw new UnsupportedOperationError(String.format("The operator %s cannot be applied to %s values", operator, type));
    }

    public NSData<?> applyCall(List<NSData<?>> arguments) {
        throw new UnsupportedOperationError(String.format("%s values cannot be called", type));
    }

    public NSData<?> applyLookup(List<NSData<?>> terms) {
        throw new UnsupportedOperationError(String.format("%s values cannot be looked up", type));
    }

    public NSData<?> applyUpdate(List<NSData<?>> terms, NSData<?> value) {
        throw new UnsupportedOperationError(String.format("%s values cannot be updated", type));
    }

    public NSData<?> applyAccess(String member) {
        throw new UnsupportedOperationError(String.format("%s values do not have any members", type));
    }

    public NSData<?> applyAssignment(String member, NSData<?> value) {
        throw new UnsupportedOperationError(String.format("%s values do not have any members", type));
    }

    public NSBoolean toNSBoolean() {
        throw new UnsupportedOperationError(String.format("%s values cannot be converted to a boolean", type));
    }

    public NSString toNSString() {
        throw new UnsupportedOperationError(String.format("%s values cannot be stringified", type));
    }

    public enum Type {
        BOOLEAN, NULL, NUMBER, STRING, CALLABLE, OBJECT, CLASS, ITERATOR, NATIVE_FUNCTION, LIST
    }
}
