package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BuiltinMethodParameter {
  boolean variableLength () default false;

  boolean optional () default false;

  String name ();

  NSData.Type[] types () default {};
}
