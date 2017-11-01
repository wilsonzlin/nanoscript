package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.VMError;

import java.util.Map;
import java.util.TreeMap;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.ReferenceError;
import static java.lang.String.format;

public class NSObject extends NSData {
    private final NSClass constructor; // Can be null
    private final Map<String, NSData> memberVariables = new TreeMap<>();
    // Cached instance methods previously found via ancestors and has `self` bound
    // are stored here; it is not initially filled
    private final Map<String, NSCallable> memberMethods = new TreeMap<>();

    private NSObject(NSClass constructor) {
        super(Type.OBJECT);
        this.constructor = constructor;
    }

    public static NSObject from(NSClass type) {
        return new NSObject(type);
    }

    public NSClass getConstructor() {
        return constructor;
    }

    public NSBoolean isInstanceOf(NSClass type) {
        return NSBoolean.from(this.constructor.isOrIsDescendantOf(type));
    }

    public void createOrUpdateMemberVariable(String name, NSData value) {
        memberVariables.put(name, value);
    }

    private NSData getOwnOrInheritedMember(String member) {
        /*
         *
         * WARNING: Lazy loading of instance variables is bad;
         * see "notes/Lazy loading of instance variables.md"
         *
         */

        // Get member variable
        if (memberVariables.containsKey(member)) {
            return memberVariables.get(member);
        }

        // Get cached instance method
        if (memberMethods.containsKey(member)) {
            return memberMethods.get(member);
        }

        if (constructor != null) {
            // Get instance method
            NSCallable methodMember = constructor.buildInstanceMethod(member, this);
            if (methodMember != null) {
                // Cache
                memberMethods.put(member, methodMember);
                return methodMember;
            }
        }

        return null;
    }

    @Override
    public NSData nsAccess(String member) {
        // Any static member cannot be accessed from an object
        NSData value = getOwnOrInheritedMember(member);
        if (value == null) {
            throw VMError.from(ReferenceError, format("The member `%s` does not exist", member));
        }
        return value;
    }

    @Override
    public void nsAssign(String member, NSData value) {
        memberVariables.put(member, value);
    }

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }
}
