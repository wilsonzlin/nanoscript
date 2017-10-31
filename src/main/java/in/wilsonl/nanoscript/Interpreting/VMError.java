package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.Data.NSObject;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class VMError extends RuntimeException {
    private final NSData<?> value;

    public VMError(NSData<?> value) {
        this.value = value;
    }

    public static VMError from(BuiltinClass type, Object... raw_args) {
        if (!type.getNSClass().matchesType(BuiltinClass.RuntimeError.getNSClass())) {
            throw new InternalStateError("Invalid type");
        }
        List<NSData<?>> args = new ROList<>();
        for (Object o : raw_args) {
            if (o == null) {
                args.add(NSNull.NULL);
            } else if (o instanceof NSData<?>) {
                args.add((NSData<?>) o);
            } else if (o instanceof String) {
                args.add(NSString.from((String) o));
            } else if (o instanceof Number) {
                args.add(NSNumber.from((Double) o));
            } else if (o instanceof NSData[]) {
                args.add(NSList.from((NSData[]) o));
            } else {
                throw new InternalStateError("Unknown argument type");
            }
        }

        NSData<?> nsError = type.getNSClass().nsCall(args);
        return new VMError(nsError);
    }

    @Override
    public String getMessage() {
        if (value instanceof NSObject && ((NSObject) value).isInstanceOf(BuiltinClass.RuntimeError.getNSClass()).getRawValue()) {
            return (String) value.nsAccess("message").getRawValue();
        } else {
            return "A VMError was thrown with a non-error-object value";
        }
    }

    public NSData<?> getValue() {
        return value;
    }
}
