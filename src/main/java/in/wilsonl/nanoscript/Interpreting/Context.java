package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;

public interface Context {
    NSData getContextSymbol(String name);

    boolean setContextSymbol(String name, NSData value);

    default void createContextSymbol(String name, NSData initialValue) {
        throw VMError.from(BuiltinClass.UnsupportedOperationError, "Variables can't be declared here");
    }
}
