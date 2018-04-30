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
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.RuntimeError;

public class VMError extends RuntimeException {
  private final NSData value;
  private final Position position;

  public VMError (NSData value) {
    this(value, null);
  }

  public VMError (NSData value, Position position) {
    super(buildMessage(value, position));
    this.value = value;
    this.position = position;
  }

  public static VMError from (Position position, BuiltinClass type, Object... raw_args) {
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
    return new VMError(nsError, position);
  }

  public static VMError from (BuiltinClass type, Object... raw_args) {
    return from(null, type, raw_args);
  }

  private static String buildMessage (NSData value, Position position) {
    String message;

    if (value instanceof NSObject && ((NSObject) value).isInstanceOf(RuntimeError.getNSClass()).isTrue()) {
      message = ((NSObject) value)
                  .getConstructor()
                  .getName() + ": " + value
                  .nsAccess("message")
                  .nsToString()
                  .getRawString();
    } else {
      message = value
        .nsToString()
        .getRawString();
    }

    if (position != null) {
      message += " [Line " + position.getLine() + ", Character " + position.getColumn() + "]";
    }

    return message;
  }

  public boolean hasPosition () {
    return position != null;
  }

  public NSData getValue () {
    return value;
  }
}
