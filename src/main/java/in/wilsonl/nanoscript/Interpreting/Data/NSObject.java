package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.VMError;

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

    public NSClass getConstructor() {
        return type;
    }

    public NSBoolean isInstanceOf(NSClass type) {
        return NSBoolean.from(this.type.matchesType(type));
    }

    private NSData<?> getOwnOrInheritedMember(String member) {
        // Get cached or own member
        if (memberVariables.containsKey(member)) {
            return memberVariables.get(member);
        }

        if (memberMethods.containsKey(member)) {
            return memberMethods.get(member);
        }

        // Get instance method
        if (type != null) {
            NSCallable methodMember = type.buildInstanceMethod(member, this);
            if (methodMember != null) {
                // Cache
                memberMethods.put(member, methodMember);
                return methodMember;
            }

            // Get instance variable
            NSData<?> varMember = type.buildInstanceVariable(member, this);
            if (varMember != null) {
                memberVariables.put(member, varMember);
                return varMember;
            }
        }

        return null;
    }

    @Override
    public NSData<?> nsAccess(String member) {
        // Ancestor static members cannot be accessed from an object
        NSData<?> value = getOwnOrInheritedMember(member);
        if (value == null) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The member `%s` does not exist", member));
        }
        return value;
    }

    @Override
    public void nsAssign(String member, NSData<?> value) {
        memberVariables.put(member, value);
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
