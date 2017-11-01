package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.Data.NSObject;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.RuntimeError;

public class VMError extends RuntimeException {
    private final NSData value;

    public VMError(NSData value) {
        this.value = value;
    }

    public static VMError from(BuiltinClass type, Object... raw_args) {
        if (!type.getNSClass().isOrIsDescendantOf(RuntimeError.getNSClass())) {
            throw new InternalStateError("Invalid type");
        }
        List<NSArgument> args = new ROList<>();
        for (Object o : raw_args) {
            NSData nsVal;
            if (o == null) {
                nsVal = NSNull.NULL;
            } else if (o instanceof NSData) {
                nsVal = (NSData) o;
            } else if (o instanceof String) {
                nsVal = NSString.from((String) o);
            } else if (o instanceof Number) {
                nsVal = NSNumber.from((Double) o);
            } else if (o instanceof NSData[]) {
                nsVal = NSList.from((NSData[]) o);
            } else {
                throw new InternalStateError("Unknown argument type");
            }
            args.add(new NSArgument(nsVal));
        }

        NSData nsError = type.getNSClass().nsCall(args);
        return new VMError(nsError);
    }

    @Override
    public String getMessage() {
        if (value instanceof NSObject && ((NSObject) value).isInstanceOf(RuntimeError.getNSClass()).isTrue()) {
            return ((NSObject) value).getConstructor().getName() + ": " + value.nsAccess("message").nsToString().getRawString();
        } else {
            return "A VMError was thrown with a non-error-object value";
        }
    }

    public NSData getValue() {
        return value;
    }
}
