package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSValidatedArguments;

// A native function takes arguments and does something using Java code
// It's not a closure and it doesn't care about context (as everything
// is native and declared in Java)
// It may still use <self> if it's an instance method of a native class
public class NSNativeCallable extends NSCallable {
  private final NSNativeCallableBody body;

  public NSNativeCallable (NSObject selfValue, ArgumentsValidator parameters, NSNativeCallableBody body) {
    super(selfValue, parameters);
    this.body = body;
  }

  public NSNativeCallable (ArgumentsValidator parameters, NSNativeSelflessCallableBody body) {
    this(null, parameters, body);
  }

  @Override
  protected NSData applyBody (NSValidatedArguments arguments) {
    return body.function(selfValue, arguments);
  }

  @Override
  protected NSCallable rebindSelf (NSObject to) {
    return new NSNativeCallable(to, parameters, body);
  }
}
