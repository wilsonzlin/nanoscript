package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Utils.ROMap;

import java.util.Map;

public class NSDataHelperMethods {
    private final Map<String, NSNativeCallable> methods = new ROMap<>();

    public void addMethod(String name, ArgumentsValidator validator, NSNativeSelflessCallableBody method) {
        methods.put(name, new NSNativeCallable(validator, method));
    }

    public NSNativeCallable getMethod(String name) throws NoSuchMethodException {
        NSNativeCallable method = methods.get(name);
        if (method == null) {
            throw new NoSuchMethodException("No such method");
        }
        return method;
    }
}
