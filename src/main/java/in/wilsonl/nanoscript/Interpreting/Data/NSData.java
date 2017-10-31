package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;

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

    private String getFriendlyTypeName(boolean capitalise) {
        String friendly = getType().name().toLowerCase();
        if (capitalise) {
            friendly = friendly.substring(0, 1).toUpperCase() + friendly.substring(1);
        }
        return friendly;
    }

    private String getFriendlyTypeName() {
        return getFriendlyTypeName(false);
    }

    public final T getRawValue() {
        return rawValue;
    }

    public NSIterator nsIterate() {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be iterated", getFriendlyTypeName(true)));
    }

    public NSData<?> nsAdd(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be added", getFriendlyTypeName(true)));
    }

    public NSData<?> nsSubtract(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be subtracted", getFriendlyTypeName(true)));
    }

    public NSData<?> nsMultiply(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be multiplied", getFriendlyTypeName(true)));
    }

    public NSData<?> nsDivide(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be divided", getFriendlyTypeName(true)));
    }

    public NSData<?> nsExponentiate(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be exponentiated", getFriendlyTypeName(true)));
    }

    public NSData<?> nsModulo(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be moduloed", getFriendlyTypeName(true)));
    }

    public NSData<?> nsApplyHashOperator() {
        throw VMError.from(UnsupportedOperationError, String.format("The hash operator cannot be applied to %s values", getFriendlyTypeName()));
    }

    public NSBoolean nsTestEquality(NSData<?> other) {
        if (getType() == Type.NULL) {
            return NSBoolean.from(equals(other));
        }
        if (other.getType() == Type.NULL) {
            return NSBoolean.from(other.equals(this));
        }
        if (other.getType() != getType()) {
            throw VMError.from(BuiltinClass.TypeError, String.format("Attempted to test equality between types %s and %s", other.getFriendlyTypeName(), getFriendlyTypeName()));
        }
        return NSBoolean.from(equals(other));
    }

    public NSNumber nsCompare(NSData<?> other) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be compared", getFriendlyTypeName(true)));
    }

    public NSData<?> nsCall(List<NSData<?>> arguments) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be called", getFriendlyTypeName(true)));
    }

    public NSData<?> nsLookup(List<NSData<?>> terms) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be looked up", getFriendlyTypeName(true)));
    }

    public void nsUpdate(List<NSData<?>> terms, NSData<?> value) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be updated", getFriendlyTypeName(true)));
    }

    public NSData<?> nsAccess(String member) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values do not have any members", getFriendlyTypeName(true)));
    }

    public void nsAssign(String member, NSData<?> value) {
        throw VMError.from(UnsupportedOperationError, String.format("%s values do not have any members", getFriendlyTypeName(true)));
    }

    public abstract NSBoolean nsToBoolean();

    public NSString nsToString() {
        throw VMError.from(UnsupportedOperationError, String.format("%s values cannot be stringified", getFriendlyTypeName(true)));
    }

    public enum Type {
        BOOLEAN, NULL, NUMBER, STRING, CALLABLE, OBJECT, CLASS, ITERATOR, LIST
    }
}
