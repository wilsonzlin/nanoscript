package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSParameter;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeClass.ClassBuilder;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator.ZERO;
import static in.wilsonl.nanoscript.Interpreting.Data.NSNull.NULL;

public enum BuiltinClass {
    RuntimeError(new ClassBuilder()
            .setConstructor(new ArgumentsValidator(null, new NSParameter("message", NSData.Type.STRING)), (self, arguments) -> {
                self.nsAssign("message", arguments.get("message").nsToString());
                return NULL;
            })
            .addInstanceVariable("message", NSString.EMPTY)
    ),

    ArgumentsError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    EndOfIterationError(new ClassBuilder()
            .addParent(RuntimeError)
            .setConstructor(ZERO, (self, arguments) -> {
                List<NSArgument> args = new ROList<>();
                args.add(new NSArgument(NSString.from("Iterator has no more values")));
                RuntimeError.getNSClass().applyConstructor(self, args);
                return NULL;
            })
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

    OutOfBoundsError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    NoSuchElementError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    KeyError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),

    ValueError(new ClassBuilder()
            .addParent(RuntimeError)
            .matchParentConstructor()
    ),;

    private final NSNativeClass nativeClass;

    BuiltinClass(ClassBuilder builder) {
        this.nativeClass = NSNativeClass.from(this.name(), builder);
    }

    public NSNativeClass getNSClass() {
        return nativeClass;
    }

}
