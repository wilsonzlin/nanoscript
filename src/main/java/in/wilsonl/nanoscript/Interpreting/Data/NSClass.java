package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator;
import in.wilsonl.nanoscript.Interpreting.VMError.ArgumentsError;
import in.wilsonl.nanoscript.Interpreting.VMError.ReferenceError;
import in.wilsonl.nanoscript.Interpreting.VMError.SyntaxError;
import in.wilsonl.nanoscript.Syntax.Class.Class;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassConstructor;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.ROSet;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NSClass extends NSData<Object> implements Context {
    private final Context parentContext; // Can be null if NSNativeClass
    private final SetOnce<String> name = new SetOnce<>();
    private final List<NSClass> parents = new ROList<>();
    private final SetOnce<NSConstructorSource> constructor = new SetOnce<>(true); // Can be null if using default constructor
    private final Map<String, NSCallable> staticMethods = new ROMap<>();
    private final Map<String, NSInstanceMethodSource> rawInstanceMethods = new ROMap<>();
    private final Map<String, NSData<?>> staticVariables = new ROMap<>();
    private final Map<String, NSInstanceVariableSource> rawInstanceVariables = new ROMap<>();
    private final Set<NSClass> ancestors = new ROSet<>();

    private NSClass(Context parentContext) {
        super(Type.CLASS, null);
        this.parentContext = parentContext;
    }

    protected NSClass(String name, List<NSClass> parents, NSNativeFunctionBody constructor, Map<String, NSNativeFunction> staticMethods, Map<String, NSNativeFunctionBody> rawInstanceMethods, Map<String, NSData<?>> staticVariables, Map<String, NSValueYielder> rawInstanceVariables) {
        // For NSNativeClass
        super(Type.CLASS, null);
        this.parentContext = null;
        this.name.set(name);
        this.parents.addAll(parents);
        this.constructor.set(constructor);
        this.staticMethods.putAll(staticMethods);
        this.rawInstanceMethods.putAll(rawInstanceMethods);
        this.staticVariables.putAll(staticVariables);
        this.rawInstanceVariables.putAll(rawInstanceVariables);
        for (NSClass p : parents) {
            ancestors.addAll(p.ancestors);
            ancestors.add(p);
        }
    }

    public static NSClass from(Context parentContext, Class st_class) {
        NSClass nsClass = new NSClass(parentContext);

        // Get name
        String name = st_class.getName().getName();
        nsClass.name.set(name);

        // TODO Load parents
        List<NSClass> parents = new ROList<>();
        for (NSClass p : parents) {
            nsClass.ancestors.addAll(p.ancestors);
            nsClass.ancestors.add(p);
        }

        // Parent context should be the global/chunk context,
        // as nested or variable classes are not allowed
        ClassConstructor st_constructor = st_class.getConstructor();
        if (st_constructor == null) {
            // Use default constructor
            // Don't check that no parent has a non-default constructor with more than zero parameters,
            // as the parent might be a NSNativeClass (also, because this is a scripting language)
            nsClass.constructor.set(null);
        } else {
            nsClass.constructor.set(st_class.getConstructor());
        }

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

    public boolean isInstance(NSObject q) {
        NSClass constructor = q.getConstructor();
        return constructor == this || ancestors.contains(constructor);
    }

    public String getName() {
        return name.get();
    }

    private boolean hasOwnStaticVariable(String name) {
        return staticVariables.containsKey(name);
    }

    private boolean hasOwnRawInstanceVariable(String name) {
        return rawInstanceVariables.containsKey(name);
    }

    private boolean hasOwnStaticMethod(String name) {
        return staticMethods.containsKey(name);
    }

    private boolean hasOwnRawInstanceMethod(String name) {
        return rawInstanceMethods.containsKey(name);
    }

    private NSData<?> getOwnStaticVariable(String name) {
        if (!hasOwnStaticVariable(name)) {
            throw new ReferenceError(String.format("The class static variable `%s` does not exist", name));
        }
        return staticVariables.get(name);
    }

    private NSInstanceVariableSource getOwnRawInstanceVariable(String name) {
        if (!hasOwnRawInstanceVariable(name)) {
            throw new ReferenceError(String.format("The class instance variable `%s` does not exist", name));
        }
        return rawInstanceVariables.get(name);
    }

    private NSCallable getOwnStaticMethod(String name) {
        if (!hasOwnStaticMethod(name)) {
            throw new ReferenceError(String.format("The class static method `%s` does not exist", name));
        }
        return staticMethods.get(name);
    }

    private NSInstanceMethodSource getOwnRawInstanceMethod(String name) {
        if (!hasOwnRawInstanceMethod(name)) {
            throw new ReferenceError(String.format("The class instance method `%s` does not exist", name));
        }
        return rawInstanceMethods.get(name);
    }

    private void setOwnStaticVariable(String name, NSData<?> value) {
        if (!hasOwnStaticVariable(name)) {
            throw new ReferenceError(String.format("The class static variable `%s` does not exist", name));
        }
        staticVariables.put(name, value);
    }

    public NSCallable buildInstanceMethod(String methodName, NSObject target) {
        RawInstanceMethodMember m = getOwnOrAncestorRawInstanceMethod(methodName);
        if (m == null) {
            return null;
        }
        NSInstanceMethodSource rawMethod = m.getMember();
        if (rawMethod instanceof NSNativeFunctionBody) {
            return new NSNativeFunction(target, (NSNativeFunctionBody) rawMethod);
        } else if (rawMethod instanceof ClassMethod) {
            LambdaExpression lambda = ((ClassMethod) rawMethod).getLambda();
            return NSCallable.from(target, lambda.getParameters(), lambda.getBody());
        } else {
            throw new InternalError("Unknown instance method source type");
        }
    }

    public NSData<?> buildInstanceVariable(String member, NSObject target) {
        NSClass.RawInstanceVariableMember varMember = getOwnOrAncestorRawInstanceVariable(member);
        if (varMember == null) {
            return null;
        }
        NSInstanceVariableSource rawVar = varMember.getMember();
        if (rawVar instanceof NSValueYielder) {
            return ((NSValueYielder) rawVar).yield();
        } else if (rawVar instanceof ClassVariable) {
            return ExpressionEvaluator.evaluateExpression(target, ((ClassVariable) rawVar).getVariable().getInitialiser());
        } else {
            throw new InternalError("Unknown instance variable source type");
        }
    }

    private RawInstanceMethodMember getOwnOrAncestorRawInstanceMethod(String methodName) {
        if (hasOwnRawInstanceMethod(methodName)) {
            return new RawInstanceMethodMember(this, getOwnRawInstanceMethod(methodName));
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

    private RawInstanceVariableMember getOwnOrAncestorRawInstanceVariable(String variableName) {
        if (hasOwnRawInstanceVariable(variableName)) {
            return new RawInstanceVariableMember(this, getOwnRawInstanceVariable(variableName));
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

    private StaticMethodMember getOwnOrAncestorStaticMethod(String methodName) {
        if (hasOwnStaticMethod(methodName)) {
            return new StaticMethodMember(this, getOwnStaticMethod(methodName));
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

    private StaticVariableMember getOwnOrAncestorStaticVariable(String variableName) {
        if (hasOwnStaticVariable(variableName)) {
            return new StaticVariableMember(this, getOwnStaticVariable(variableName));
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

    private void applyConstructor(NSObject target, List<NSData<?>> arguments) {
        if (constructor.get() == null) {
            if (arguments != null && arguments.size() != 0) {
                throw new ArgumentsError("Default constructor does not take arguments");
            }
            for (NSClass p : parents) {
                p.applyConstructor(target, null);
            }
        } else {
            NSConstructorSource rawConstructor = constructor.get();
            NSData<?> evaluationResult;
            if (arguments == null) {
                arguments = new ROList<>();
            }
            if (rawConstructor instanceof NSNativeFunctionBody) {
                NSNativeFunction constructor = new NSNativeFunction(target, (NSNativeFunctionBody) rawConstructor);
                evaluationResult = constructor.applyCall(arguments);
            } else if (rawConstructor instanceof ClassConstructor) {
                LambdaExpression lambda = ((ClassConstructor) rawConstructor).getLambda();
                NSCallable constructor = NSCallable.from(target, lambda.getParameters(), lambda.getBody());
                evaluationResult = constructor.applyCall(arguments);
            } else {
                throw new InternalError("Unrecognised constructor source type");
            }
            if (evaluationResult != NSNull.NULL) {
                throw new SyntaxError("Can't return from a constructor");
            }
        }
    }

    @Override
    public NSData<?> applyCall(List<NSData<?>> arguments) {
        NSObject newObject = NSObject.from(this);
        applyConstructor(newObject, arguments);
        return newObject;
    }

    @Override
    public NSData<?> applyAccess(String member) {
        // This will throw exception if it doesn't exist
        return getOwnStaticVariable(member);
    }

    @Override
    public void applyAssignment(String member, NSData<?> value) {
        // This will throw exception if it doesn't exist
        setOwnStaticVariable(member, value);
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
        StaticVariableMember staticVar = getOwnOrAncestorStaticVariable(name);
        if (staticVar != null) {
            staticVar.getOwner().setOwnStaticVariable(name, value);
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

    public static class RawInstanceMethodMember extends Member<NSInstanceMethodSource> {
        public RawInstanceMethodMember(NSClass owner, NSInstanceMethodSource member) {
            super(owner, member);
        }
    }

    public static class RawInstanceVariableMember extends Member<NSInstanceVariableSource> {
        public RawInstanceVariableMember(NSClass owner, NSInstanceVariableSource member) {
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
