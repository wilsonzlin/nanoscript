package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.List;

// A native function takes arguments and does something using Java code
// It's not a closure, but it can be an instance method and so could have a <selfValue>
// Like a NSCallable, a new instance is created for each object, if
// it's an instance method
public class NSNativeFunction extends NSCallable {
    private final NSObject selfValue; // Can be null
    private final NSNativeFunctionBody body;

    public NSNativeFunction(NSObject selfValue, NSNativeFunctionBody body) {
        super();
        this.selfValue = selfValue;
        this.body = body;
    }

    public NSNativeFunction(NSNativeFunctionBody body) {
        this(null, body);
    }

    @Override
    public NSData<?> nsCall(List<NSData<?>> arguments) {
        return body.run(selfValue, arguments);
    }
}
