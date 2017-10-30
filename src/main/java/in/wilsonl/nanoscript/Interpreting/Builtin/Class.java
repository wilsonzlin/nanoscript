package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Data.NSClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunctionBody;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Interpreting.Data.NSValueYielder;
import in.wilsonl.nanoscript.Interpreting.VMError.ArgumentsError;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;

public enum Class {
    RuntimeError(new Builder()
            .setConstructor((self, arguments) -> {
                ArgumentsError.assertArgumentsMatch(1, arguments);
                self.applyAssignment("message", arguments.get(0).toNSString());
                return NSNull.NULL;
            })
            .addInstanceVariable("message", () -> NSString.EMPTY)
    );

    private final NSNativeClass nativeClass;

    Class(Builder builder) {
        this.nativeClass = new NSNativeClass(this.name(), builder.getParents(), builder.getConstructor(), builder.getStaticMethods(), builder.getRawInstanceMethods(), builder.getStaticVariables(), builder.getRawInstanceVariables());
    }

    public NSNativeClass getNativeClass() {
        return nativeClass;
    }

    private static class Builder {
        private final List<NSClass> parents = new ROList<>();
        private final SetOnce<NSNativeFunctionBody> constructor = new SetOnce<>(true);
        private final Map<String, NSNativeFunction> staticMethods = new ROMap<>();
        private final Map<String, NSNativeFunctionBody> rawInstanceMethods = new ROMap<>();
        private final Map<String, NSData<?>> staticVariables = new ROMap<>();
        private final Map<String, NSValueYielder> rawInstanceVariables = new ROMap<>();

        public Builder addParent(NSClass parent) {
            parents.add(parent);
            return this;
        }

        public Builder addStaticMethod(String name, NSNativeFunction method) {
            staticMethods.put(name, method);
            return this;
        }

        public Builder addInstanceMethod(String name, NSNativeFunctionBody method) {
            rawInstanceMethods.put(name, method);
            return this;
        }

        public Builder addStaticVariable(String name, NSData<?> value) {
            staticVariables.put(name, value);
            return this;
        }

        public Builder addInstanceVariable(String name, NSValueYielder valueYielder) {
            rawInstanceVariables.put(name, valueYielder);
            return this;
        }

        public List<NSClass> getParents() {
            return parents;
        }

        public NSNativeFunctionBody getConstructor() {
            return constructor.get();
        }

        public Builder setConstructor(NSNativeFunctionBody constructor) {
            this.constructor.set(constructor);
            return this;
        }

        public Map<String, NSNativeFunction> getStaticMethods() {
            return staticMethods;
        }

        public Map<String, NSNativeFunctionBody> getRawInstanceMethods() {
            return rawInstanceMethods;
        }

        public Map<String, NSData<?>> getStaticVariables() {
            return staticVariables;
        }

        public Map<String, NSValueYielder> getRawInstanceVariables() {
            return rawInstanceVariables;
        }
    }
}
