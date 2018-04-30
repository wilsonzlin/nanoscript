package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSParameter;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeCallable;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeSelflessCallableBody;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;

import java.util.ArrayList;
import java.util.List;

public enum BuiltinFunction {
  print(ArgumentsValidator.ANY, (__) -> {
    List<NSData> arguments = ((NSList) __.get("values")).getRawList();
    if (arguments.size() == 0) {
      System.out.println();
    } else {
      for (int i = 0; i < arguments.size() - 1; i++) {
        System.out.print(arguments.get(i).nsToString().getRawString() + ", ");
      }
      System.out.println(arguments.get(arguments.size() - 1).nsToString().getRawString());
    }

    return NSNull.NULL;
  }),
  range(new ArgumentsValidator(null, new NSParameter[]{
    new NSParameter("min", NSData.Type.NUMBER),
    new NSParameter(true, "max", NSData.Type.NUMBER),
    new NSParameter(true, "step", NSData.Type.NUMBER)
  }), (arguments) -> {
    long max = ((NSNumber) arguments.get("min")).toInt();
    long min;
    long step;
    if (arguments.get("max").nsIsNotNull()) {
      min = max;
      max = ((NSNumber) arguments.get("max")).toInt();
    } else {
      min = 0;
    }
    if (arguments.get("step").nsIsNotNull()) {
      step = ((NSNumber) arguments.get("step")).toInt();
    } else {
      step = 1;
    }

    List<NSData> res = new ArrayList<>();
    for (long i = min; i < max; i += step) {
      res.add(NSNumber.from(i));
    }

    return NSList.from(res);
  }),
  str(new ArgumentsValidator(
    null,
    new NSParameter("value")),
    (arguments) -> arguments.get("value").nsToString());

  private final NSNativeCallable function;

  BuiltinFunction (ArgumentsValidator validator, NSNativeSelflessCallableBody body) {
    this.function = new NSNativeCallable(validator, body);
  }

  public NSNativeCallable getFunction () {
    return function;
  }
}
