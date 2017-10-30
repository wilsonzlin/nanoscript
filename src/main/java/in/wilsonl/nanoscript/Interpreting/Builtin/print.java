package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;

import java.util.List;

public class print {
    public static final NSNativeFunction print = createPrintFunction();

    private static NSNativeFunction createPrintFunction() {
        return new NSNativeFunction() {
            @Override
            public NSData<?> applyCall(List<NSData<?>> arguments) {
                for (int i = 0; i < arguments.size() - 1; i++) {
                    System.out.print(arguments.get(i).toNSString().getRawValue() + ", ");
                }
                System.out.println(arguments.get(arguments.size() - 1).toNSString().getRawValue());

                return NSNull.NULL;
            }
        };
    }
}
