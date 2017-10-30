package in.wilsonl.nanoscript.Interpreting.VMError;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

public class ExplicitlyThrownError extends RuntimeError {
    public ExplicitlyThrownError(NSData<?> value) {
        super(value.toNSString().getRawValue());
    }
}
