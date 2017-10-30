package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Exception.ReferenceError;
import in.wilsonl.nanoscript.Syntax.Class.Class;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassConstructor;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;

public class NSClass extends NSData<Object> implements Context {
    private final Context parentContext; // Can be null
    private final String name;
    private final SetOnce<ClassConstructor> constructor = new SetOnce<>();
    private final List<NSClass> parents = new ROList<>();
    private final Map<String, NSCallable> staticMethods = new ROMap<>();
    private final Map<String, ClassMethod> rawInstanceMethods = new ROMap<>();
    private final Map<String, NSData<?>> staticVariables = new ROMap<>();
    private final Map<String, ClassVariable> rawInstanceVariables = new ROMap<>();

    private NSClass(Context parentContext, String name) {
        super(Type.CLASS, null);
        this.parentContext = parentContext;
        this.name = name;
    }

    public static NSClass from(Context parentContext, Class st_class) {
        // Get name
        String name = st_class.getName().getName();
        // Parent context should be the global/chunk context,
        // as nested or variable classes are not allowed
        NSClass nsClass = new NSClass(parentContext, name);
        // TODO `supers SomeParent(constructor_arg_1)`
        nsClass.constructor.set(st_class.getConstructor());

        // TODO Load parents
        List<NSClass> parents = new ROList<>();

        // Process methods
        for (ClassMethod st_method : st_class.getMethods()) {
            String methodName = st_method.getName().getName();
            if (st_method.isStatic()) {
                NSCallable callable = NSCallable.from(nsClass, st_method.getLambda().getParameters(), st_method.getLambda().getBody());
                nsClass.staticMethods.put(methodName, callable);
            } else {
                nsClass.rawInstanceMethods.put(methodName, st_method);
            }
        }

        // Process variables
        for (ClassVariable st_var : st_class.getVariables()) {
            String varName = st_var.getVariable().getName().getName();
            if (st_var.isStatic()) {
                // Order matters
                NSData<?> value = nsClass.evaluateExpressionInContext(st_var.getVariable().getInitialiser());
                nsClass.staticVariables.put(varName, value);
            } else {
                nsClass.rawInstanceVariables.put(varName, st_var);
            }
        }

        return nsClass;
    }

    public void setStaticVariable(String name, NSData<?> value) {
        if (!staticVariables.containsKey(name)) {
            throw new ReferenceError(String.format("The class static variable `%s` does not exist", name));
        }
        staticVariables.put(name, value);
    }

    public RawInstanceMethodMember getOwnOrAncestorRawInstanceMethod(String methodName) {
        if (rawInstanceMethods.containsKey(methodName)) {
            return new RawInstanceMethodMember(this, rawInstanceMethods.get(methodName));
        }
        RawInstanceMethodMember method;
        for (NSClass parent : parents) {
            method = parent.getOwnOrAncestorRawInstanceMethod(methodName);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public RawInstanceVariableMember getOwnOrAncestorRawInstanceVariable(String variableName) {
        if (rawInstanceVariables.containsKey(variableName)) {
            return new RawInstanceVariableMember(this, rawInstanceVariables.get(variableName));
        }
        RawInstanceVariableMember var;
        for (NSClass parent : parents) {
            var = parent.getOwnOrAncestorRawInstanceVariable(variableName);
            if (var != null) {
                return var;
            }
        }
        return null;
    }

    public StaticMethodMember getOwnOrAncestorStaticMethod(String methodName) {
        if (staticMethods.containsKey(methodName)) {
            return new StaticMethodMember(this, staticMethods.get(methodName));
        }
        StaticMethodMember method;
        for (NSClass parent : parents) {
            method = parent.getOwnOrAncestorStaticMethod(methodName);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public StaticVariableMember getOwnOrAncestorStaticVariable(String variableName) {
        if (staticVariables.containsKey(variableName)) {
            return new StaticVariableMember(this, staticVariables.get(variableName));
        }
        StaticVariableMember var;
        for (NSClass parent : parents) {
            var = parent.getOwnOrAncestorStaticVariable(variableName);
            if (var != null) {
                return var;
            }
        }
        return null;
    }

    public NSObject instantiate() {
        return NSObject.from(this);
    }

    @Override
    public NSData<?> applyCall(List<NSData<?>> arguments) {
        NSObject newObject = NSObject.from(this);
        LambdaExpression rawConstructor = constructor.get().getLambda();
        NSCallable constructor = NSCallable.from(newObject, rawConstructor.getParameters(), rawConstructor.getBody());
        constructor.applyCall(arguments);
        return newObject;
    }

    @Override
    public NSData<?> applyAccess(String member) {
        if (staticVariables.containsKey(name)) {
            return staticVariables.get(name);
        }
        throw new ReferenceError(String.format("The static member `%s` does not exist", member));
    }

    @Override
    public NSData<?> applyAssignment(String member, NSData<?> value) {
        if (staticVariables.containsKey(name)) {
            staticVariables.put(name, value);
        }
        throw new ReferenceError(String.format("The static member `%s` does not exist", member));
    }

    @Override
    public NSData<?> getContextSymbol(String name) {
        StaticMethodMember staticMethod = getOwnOrAncestorStaticMethod(name);
        if (staticMethod != null) {
            return staticMethod.getMember();
        }

        StaticVariableMember staticVar = getOwnOrAncestorStaticVariable(name);
        if (staticVar != null) {
            return staticVar.getMember();
        }

        if (parentContext != null) {
            return parentContext.getContextSymbol(name);
        }

        return null;
    }

    @Override
    public boolean setContextSymbol(String name, NSData<?> value) {
        StaticVariableMember classContainingVar = getOwnOrAncestorStaticVariable(name);
        if (classContainingVar != null) {
            classContainingVar.getOwner().setStaticVariable(name, value);
            return true;
        }

        return parentContext != null && parentContext.setContextSymbol(name, value);

    }

    public abstract static class Member<T> {
        private final NSClass owner;
        private final T member;

        public Member(NSClass owner, T member) {
            this.owner = owner;
            this.member = member;
        }

        public NSClass getOwner() {
            return owner;
        }

        public T getMember() {
            return member;
        }
    }

    public static class RawInstanceMethodMember extends Member<ClassMethod> {
        public RawInstanceMethodMember(NSClass owner, ClassMethod member) {
            super(owner, member);
        }
    }

    public static class RawInstanceVariableMember extends Member<ClassVariable> {
        public RawInstanceVariableMember(NSClass owner, ClassVariable member) {
            super(owner, member);
        }
    }

    public static class StaticMethodMember extends Member<NSCallable> {
        public StaticMethodMember(NSClass owner, NSCallable member) {
            super(owner, member);
        }
    }

    public static class StaticVariableMember extends Member<NSData<?>> {
        public StaticVariableMember(NSClass owner, NSData<?> member) {
            super(owner, member);
        }
    }
}
