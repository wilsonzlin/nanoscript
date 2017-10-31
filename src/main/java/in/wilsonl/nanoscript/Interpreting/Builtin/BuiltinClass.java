package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Data.NSClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunctionBody;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Interpreting.Data.NSValueYielder;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;

public enum BuiltinClass {
    RuntimeError(new ClassBuilder()
            .setConstructor(NSNativeFunctionBody.withValidator(ArgumentsValidator.ONE, (self, arguments) -> {
                self.nsAssign("message", arguments.get(0).nsToString());
                return NSNull.NULL;
            }))
            .addInstanceVariable("message", () -> NSString.EMPTY)
    ),

    ArgumentsError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    EndOfIterationError(new ClassBuilder()
            .addParent(RuntimeError)
            .setConstructor(NSNativeFunctionBody.withValidator(ArgumentsValidator.ZERO, (self, arguments) -> {
                List<NSData<?>> args = new ROList<>();
                args.add(NSString.from("Iterator has no more values"));
                RuntimeError.getNSClass().applyConstructor(self, args);
                return NSNull.NULL;
            }))
    ),

    ReferenceError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    SyntaxError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    TypeError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    UnsupportedOperationError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    ValueError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),;

    private final NSNativeClass nativeClass;

    BuiltinClass(ClassBuilder builder) {
        this.nativeClass = new NSNativeClass(this.name(), builder.getParents(), builder.getConstructor(), builder.getStaticMethods(), builder.getRawInstanceMethods(), builder.getStaticVariables(), builder.getRawInstanceVariables());
    }

    public NSNativeClass getNSClass() {
        return nativeClass;
    }

    private static class ClassBuilder {
        private final List<NSClass> parents = new ROList<>();
        private final SetOnce<NSNativeFunctionBody> constructor = new SetOnce<>(true);
        private final Map<String, NSNativeFunction> staticMethods = new ROMap<>();
        private final Map<String, NSNativeFunctionBody> rawInstanceMethods = new ROMap<>();
        private final Map<String, NSData<?>> staticVariables = new ROMap<>();
        private final Map<String, NSValueYielder> rawInstanceVariables = new ROMap<>();

        public ClassBuilder addParent(BuiltinClass parent) {
            parents.add(parent.getNSClass());
            return this;
        }

        public ClassBuilder addParent(NSClass parent) {
            parents.add(parent);
            return this;
        }

        public ClassBuilder addStaticMethod(String name, NSNativeFunction method) {
            staticMethods.put(name, method);
            return this;
        }

        public ClassBuilder addInstanceMethod(String name, NSNativeFunctionBody method) {
            rawInstanceMethods.put(name, method);
            return this;
        }

        public ClassBuilder addStaticVariable(String name, NSData<?> value) {
            staticVariables.put(name, value);
            return this;
        }

        public ClassBuilder addInstanceVariable(String name, NSValueYielder valueYielder) {
            rawInstanceVariables.put(name, valueYielder);
            return this;
        }

        public List<NSClass> getParents() {
            return parents;
        }

        public NSNativeFunctionBody getConstructor() {
            return constructor.isSet() ? constructor.get() : null;
        }

        public ClassBuilder setConstructor(NSNativeFunctionBody constructor) {
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

        public ClassBuilder matchParentConstructor() {
            if (parents.size() != 1) {
                throw new InternalStateError("Native class does not have exactly one parent");
            }
            NSClass parent = parents.get(0);
            constructor.set((self, arguments) -> {
                parent.applyConstructor(self, arguments);
                return NSNull.NULL;
            });
            return this;
        }
    }
}
