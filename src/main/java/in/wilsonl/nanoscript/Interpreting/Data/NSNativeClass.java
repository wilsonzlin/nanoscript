package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.List;
import java.util.Map;

// NSNativeClass does not have a context or ClassConstructor, but should still use
// the various member management methods and maps, so that they continue to
// work seamlessly with non-native classes
public class NSNativeClass extends NSClass {
    public NSNativeClass(String name, List<NSClass> parents, NSNativeFunctionBody constructor, Map<String, NSNativeFunction> staticMethods, Map<String, NSNativeFunctionBody> rawInstanceMethods, Map<String, NSData<?>> staticVariables, Map<String, NSValueYielder> rawInstanceVariables) {
        super(name, parents, constructor, staticMethods, rawInstanceMethods, staticVariables, rawInstanceVariables);
    }
}
