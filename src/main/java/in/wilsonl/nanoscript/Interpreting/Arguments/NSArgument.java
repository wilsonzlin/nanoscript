package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

public class NSArgument {
    private final boolean optional;
    private final NSData value;

    public NSArgument(boolean optional, NSData value) {
        this.optional = optional;
        this.value = value;
    }

    public NSArgument(NSData value) {
        this(false, value);
    }

    public boolean isOptional() {
        return optional;
    }

    public NSData getValue() {
        return value;
    }
}
