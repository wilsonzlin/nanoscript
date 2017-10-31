package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.Operator;

import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.UnsupportedOperationError;

public abstract class NSData<T> {
    private final Type type;
    private final T rawValue;

    protected NSData(Type type, T rawValue) {
        this.type = type;
        this.rawValue = rawValue;
    }

    public final Type getType() {
        return type;
    }

    public final T getRawValue() {
        return rawValue;
    }

    public NSIterator nsIterate() {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be iterated", type));
    }

    // This method is **not** called when the operator is LOOKUP, NULL_LOOKUP, CALL, NULL_CALL, NOT
    public NSData<?> nsApplyUnaryOperator(Operator operator) {
        throw VMError.from(UnsupportedOperationError, String.format("The operator %s cannot be applied to %s values", operator, type));
    }

    // This method is **not** called when the operator is ACCESSOR, NULL_ACCESSOR, EQ, NEQ, LT, LEQ, GT, GEQ, SPACESHIP, INSTANCE_OF, NOT_INSTANCE_OF, AND, OR, NULL_COALESCING, ASSIGNMENT
    public NSData<?> nsApplyBinaryOperator(Operator operator, NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("The operator %s cannot be applied to %s values", operator, type));
    }

    public NSBoolean nsTestEquality(NSData<?> other) {
        return NSBoolean.from(equals(other));
    }

    public NSNumber nsCompare(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format(" %s values cannot be compared", type));
    }

    public NSData<?> nsCall(List<NSData<?>> arguments) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be called", type));
    }

    public NSData<?> nsLookup(List<NSData<?>> terms) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be looked up", type));
    }

    public void nsUpdate(List<NSData<?>> terms, NSData<?> value) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be updated", type));
    }

    public NSData<?> nsAccess(String member) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values do not have any members", type));
    }

    public void nsAssign(String member, NSData<?> value) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values do not have any members", type));
    }

    public NSBoolean nsToBoolean() {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be converted to a boolean", type));
    }

    public NSString nsToString() {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be stringified", type));
    }

    public enum Type {
        BOOLEAN, NULL, NUMBER, STRING, CALLABLE, OBJECT, CLASS, ITERATOR, LIST
    }
}
