package in.wilsonl.nanoscript.Interpreting.Builtin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BuiltinMethod {
    String name();
}
