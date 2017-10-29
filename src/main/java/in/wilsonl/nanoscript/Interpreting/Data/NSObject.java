package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Exception.ReferenceError;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;

import java.util.Map;
import java.util.TreeMap;

// The context for a NSObject is not the object itself, but rather instance
// methods and variables declared in the class <type>
// Expressions can't actually be evaluated on an object
public class NSObject extends NSData<Object> implements Context {
    private final NSClass type; // Can be null
    private final Map<String, NSData<?>> memberVariables = new TreeMap<>();
    // Cached instance methods previously found via ancestors and has `self` bound
    // are stored here; it is not initially filled
    private final Map<String, NSCallable> memberMethods = new TreeMap<>();

    private NSObject(NSClass type) {
        super(Type.OBJECT, null);
        this.type = type;
    }

    public static NSObject from(NSClass type) {
        return new NSObject(type);
    }

    public NSData<?> getOwnOrInheritedMember(String member) {
        // Get cached or own member
        if (memberVariables.containsKey(member)) {
            return memberVariables.get(member);
        }

        if (memberMethods.containsKey(member)) {
            return memberMethods.get(member);
        }

        // Get instance method
        if (type != null) {
            NSClass.RawInstanceMethodMember methodMember = type.getOwnOrAncestorRawInstanceMethod(member);
            if (methodMember != null) {
                ClassMethod rawMethod = methodMember.getMember();
                NSCallable method = NSCallable.from(this, rawMethod.getLambda().getParameters(), rawMethod.getLambda().getBody());
                // Cache
                memberMethods.put(member, method);
                return method;
            }

            // Get instance variable
            NSClass.RawInstanceVariableMember varMember = type.getOwnOrAncestorRawInstanceVariable(member);
            if (varMember != null) {
                ClassVariable rawVar = varMember.getMember();
                NSData<?> value = evaluateExpressionInContext(rawVar.getVariable().getInitialiser());
                memberVariables.put(member, value);
                return value;
            }
        }

        return null;
    }

    @Override
    public NSData<?> applyAccess(String member) {
        // Ancestor static members cannot be accessed from an object
        NSData<?> value = getOwnOrInheritedMember(member);
        if (value == null) {
            throw new ReferenceError(String.format("The member `%s` does not exist", member));
        }
        return value;
    }

    @Override
    public NSData<?> applyAssignment(String member, NSData<?> value) {
        memberVariables.put(member, value);
        return value;
    }

    @Override
    public NSData<?> getContextSymbol(String name) {
        if (name.equals("self")) {
            return this;
        }

        NSData<?> value = getOwnOrInheritedMember(name);
        if (value != null) {
            return value;
        }

        return type == null ? null : type.getContextSymbol(name);
    }

    @Override
    public boolean setContextSymbol(String name, NSData<?> value) {
        // To create new own members, use `self.new_member = new_value`
        if (memberVariables.containsKey(name)) {
            memberVariables.put(name, value);
            return true;
        }

        return type != null && type.setContextSymbol(name, value);
    }
}
