package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.NSValidatedArguments;

public interface NSNativeCallableBody {
  NSData function (NSObject self, NSValidatedArguments arguments);
}
