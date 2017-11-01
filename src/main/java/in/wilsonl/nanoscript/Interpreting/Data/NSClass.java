package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.ROSet;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class NSClass extends NSData {
    private final SetOnce<String> name = new SetOnce<>();
    private final List<NSClass> parents = new ROList<>();
    // <constructor> needs to be rebound
    private final SetOnce<NSCallable> constructor = new SetOnce<>(true, null); // Can be null if using default constructor (whether native class or not)
    private final Map<String, NSCallable> staticMethods = new ROMap<>();
    // rawInstanceMethods NSCallable values need to be rebound
    private final Map<String, NSCallable> rawInstanceMethods = new ROMap<>();
    private final Map<String, NSData> staticVariables = new ROMap<>();
    private final Set<NSClass> ancestors = new ROSet<>();

    protected NSClass() {
        super(Type.CLASS);
    }

    protected final void addParent(NSClass parent) {
        parents.add(parent);
        ancestors.addAll(parent.ancestors);
        ancestors.add(parent);
    }

    protected final void addStaticMethod(String name, NSCallable method) {
        staticMethods.put(name, method);
    }

    protected final void addInstanceMethod(String name, NSCallable method) {
        rawInstanceMethods.put(name, method);
    }

    protected final void addStaticVariable(String name, NSData value) {
        staticVariables.put(name, value);
    }

    public final boolean isOrIsDescendantOf(NSClass type) {
        return type == this || ancestors.contains(type);
    }

    public final String getName() {
        return name.get();
    }

    protected final void setName(String name) {
        this.name.set(name);
    }

    // So that descendants can match parent's constructor
    public final NSCallable getConstructor() {
        return constructor.get();
    }

    protected final void setConstructor(NSCallable constructor) {
        this.constructor.set(constructor);
    }

    private boolean hasOwnStaticVariable(String name) {
        return staticVariables.containsKey(name);
    }

    private boolean hasOwnStaticMethod(String name) {
        return staticMethods.containsKey(name);
    }

    private boolean hasOwnRawInstanceMethod(String name) {
        return rawInstanceMethods.containsKey(name);
    }

    private NSData getOwnStaticVariable(String name) {
        if (!hasOwnStaticVariable(name)) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The class static variable `%s` does not exist", name));
        }
        return staticVariables.get(name);
    }

    private NSCallable getOwnStaticMethod(String name) {
        if (!hasOwnStaticMethod(name)) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The class static method `%s` does not exist", name));
        }
        return staticMethods.get(name);
    }

    private NSCallable getOwnRawInstanceMethod(String name) {
        if (!hasOwnRawInstanceMethod(name)) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The class instance method `%s` does not exist", name));
        }
        return rawInstanceMethods.get(name);
    }

    private void setOwnStaticVariable(String name, NSData value) {
        if (!hasOwnStaticVariable(name)) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The class static variable `%s` does not exist", name));
        }
        staticVariables.put(name, value);
    }

    public final NSCallable buildInstanceMethod(String methodName, NSObject target) {
        NSCallable m = getOwnOrAncestorRawInstanceMethod(methodName);
        if (m == null) {
            return null;
        }
        return m.rebindSelf(target);
    }

    protected abstract void applyOwnInstanceVariables(NSObject target);

    private void initialiseInstanceVariables(NSObject target) {
        for (NSClass p : parents) {
            p.initialiseInstanceVariables(target);
        }
        applyOwnInstanceVariables(target);
    }

    private NSCallable getOwnOrAncestorRawInstanceMethod(String methodName) {
        if (hasOwnRawInstanceMethod(methodName)) {
            return getOwnRawInstanceMethod(methodName);
        }
        NSCallable method;
        for (NSClass parent : parents) {
            method = parent.getOwnOrAncestorRawInstanceMethod(methodName);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public final void applyConstructor(NSObject target, List<NSArgument> arguments) {
        if (constructor.get() == null) {
            if (arguments != null && arguments.size() != 0) {
                throw VMError.from(BuiltinClass.ArgumentsError, "Default constructor does not take arguments");
            }
            for (NSClass p : parents) {
                p.applyConstructor(target, null);
            }
        } else {
            if (arguments == null) {
                arguments = new ROList<>();
            }
            NSData evaluationResult = constructor.get().rebindSelf(target).nsCall(arguments);
            if (evaluationResult != NSNull.NULL) {
                throw VMError.from(BuiltinClass.SyntaxError, "Can't return from a constructor");
            }
        }
    }

    @Override
    public final NSData nsCall(List<NSArgument> arguments) {
        NSObject newObject = NSObject.from(this);
        applyConstructor(newObject, arguments);
        return newObject;
    }

    @Override
    public final NSData nsAccess(String member) {
        if (hasOwnStaticMethod(member)) {
            return getOwnStaticMethod(member);
        }
        // This will throw exception if it doesn't exist
        return getOwnStaticVariable(member);
    }

    @Override
    public final void nsAssign(String member, NSData value) {
        // This will throw exception if it doesn't exist
        setOwnStaticVariable(member, value);
    }

    @Override
    public final NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }
}
