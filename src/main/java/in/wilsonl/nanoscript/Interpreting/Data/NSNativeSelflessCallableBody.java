package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.NSValidatedArguments;

public interface NSNativeSelflessCallableBody extends NSNativeCallableBody {
  @Override
  default NSData function (NSObject self, NSValidatedArguments arguments) {
    return selflessFunction(arguments);
  }

  NSData selflessFunction (NSValidatedArguments arguments);
}
