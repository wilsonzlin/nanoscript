package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.ROSet;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Map;
import java.util.Set;

// NSNativeClass does not have a context or ClassConstructor, but should still use
// the various member management methods and maps, so that they continue to
// work seamlessly with non-native classes
public class NSNativeClass extends NSClass {
  private final Set<NSNativeClassInstanceVariable> rawInstanceVariables = new ROSet<>();

  private NSNativeClass () {
    super();
  }

  public static NSNativeClass from (String name, ClassBuilder blueprint) {
    NSNativeClass nsNativeClass = new NSNativeClass();

    // Set name
    nsNativeClass.setName(name);

    // Add parents
    for (NSClass p : blueprint.parents) {
      nsNativeClass.addParent(p);
    }

    // Set constructor
    NSCallable constructor = blueprint.constructor.get();
    if (constructor != null) {
      // `null` selfValue will be rebound when called
      nsNativeClass.setConstructor(constructor);
    }

    // Process static methods
    for (Map.Entry<String, NSNativeCallable> staticMethod : blueprint.staticMethods.entrySet()) {
      nsNativeClass.addStaticMethod(staticMethod.getKey(), staticMethod.getValue());
    }

    // Process instance methods
    for (Map.Entry<String, NSNativeCallable> instanceMethod : blueprint.rawInstanceMethods.entrySet()) {
      nsNativeClass.addInstanceMethod(instanceMethod.getKey(), instanceMethod.getValue());
    }

    // Process static variables
    for (Map.Entry<String, NSData> staticVar : blueprint.staticVariables.entrySet()) {
      nsNativeClass.addStaticVariable(staticVar.getKey(), staticVar.getValue());
    }

    // Process instance variables
    for (NSNativeClassInstanceVariable instanceVar : blueprint.rawInstanceVariables) {
      nsNativeClass.addInstanceVariable(instanceVar);
    }

    return nsNativeClass;
  }

  @Override
  protected void applyOwnInstanceVariables (NSObject target) {
    for (NSNativeClassInstanceVariable v : rawInstanceVariables) {
      target.createOrUpdateMemberVariable(
        v.getName(),
        v.getInitialValue().nsClone());
    }
  }

  private void addInstanceVariable (NSNativeClassInstanceVariable classVariable) {
    rawInstanceVariables.add(classVariable);
  }

  public static class ClassBuilder {
    private final List<NSClass> parents = new ROList<>();
    private final SetOnce<NSCallable> constructor = new SetOnce<>(true, null);
    private final Map<String, NSNativeCallable> staticMethods = new ROMap<>();
    private final Map<String, NSNativeCallable> rawInstanceMethods = new ROMap<>();
    private final Map<String, NSData> staticVariables = new ROMap<>();
    private final Set<NSNativeClassInstanceVariable> rawInstanceVariables = new ROSet<>();

    public ClassBuilder addParent (BuiltinClass parent) {
      parents.add(parent.getNSClass());
      return this;
    }

    public ClassBuilder addParent (NSClass parent) {
      parents.add(parent);
      return this;
    }

    public ClassBuilder addStaticMethod (String name, ArgumentsValidator parameters, NSNativeSelflessCallableBody method) {
      staticMethods.put(name, new NSNativeCallable(parameters, method));
      return this;
    }

    public ClassBuilder addInstanceMethod (String name, ArgumentsValidator parameters, NSNativeCallableBody method) {
      // `null` selfValue will be rebound when called
      rawInstanceMethods.put(name, new NSNativeCallable(null, parameters, method));
      return this;
    }

    public ClassBuilder addStaticVariable (String name, NSData value) {
      staticVariables.put(name, value);
      return this;
    }

    public ClassBuilder addInstanceVariable (String name, NSData initialValue) {
      rawInstanceVariables.add(new NSNativeClassInstanceVariable(name, initialValue));
      return this;
    }

    public ClassBuilder setConstructor (ArgumentsValidator parameters, NSNativeCallableBody constructor) {
      // `null` selfValue will be rebound when called
      this.constructor.set(new NSNativeCallable(null, parameters, constructor));
      return this;
    }

    public ClassBuilder matchParentConstructor () {
      if (parents.size() != 1) {
        throw new InternalStateError("Native class does not have exactly one parent");
      }
      NSClass parent = parents.get(0);
      constructor.set(parent.getRawConstructor());
      return this;
    }
  }
}
