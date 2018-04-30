package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class NSArgument {
  private final boolean optional;
  private final NSData value;

  public NSArgument (boolean optional, NSData value) {
    this.optional = optional;
    this.value = value;
  }

  public NSArgument (NSData value) {
    this(false, value);
  }

  // Provide null to specify arguments after are optional
  public static List<NSArgument> buildArguments (NSData... values) {
    List<NSArgument> args = new ROList<>();
    boolean startedOptional = false;
    for (NSData v : values) {
      if (v == null) {
        startedOptional = true;
      } else {
        args.add(new NSArgument(startedOptional, v));
      }
    }
    return args;
  }

  public boolean isOptional () {
    return optional;
  }

  public NSData getValue () {
    return value;
  }
}
