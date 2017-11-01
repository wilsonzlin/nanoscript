package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSValidatedArguments;

import java.util.List;

// REMEMBER: A callable never loses its context
public abstract class NSCallable extends NSData {
    protected final NSObject selfValue; // Can be null
    protected final ArgumentsValidator parameters;

    protected NSCallable(NSObject selfValue, ArgumentsValidator parameters) {
        super(Type.CALLABLE);
        this.selfValue = selfValue;
        this.parameters = parameters;
    }

    protected abstract NSData applyBody(NSValidatedArguments arguments);

    protected abstract NSCallable rebindSelf(NSObject to);

    @Override
    public final NSData nsCall(List<NSArgument> arguments) {
        NSValidatedArguments match = parameters.match(arguments);
        return applyBody(match);
    }

    @Override
    public final NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }
}
