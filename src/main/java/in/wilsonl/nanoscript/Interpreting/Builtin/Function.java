package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunctionBody;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.VMError.ArgumentsError;

import java.util.ArrayList;
import java.util.List;

public enum Function {
    print((self, arguments) -> {
        for (int i = 0; i < arguments.size() - 1; i++) {
            System.out.print(arguments.get(i).toNSString().getRawValue() + ", ");
        }
        System.out.println(arguments.get(arguments.size() - 1).toNSString().getRawValue());

        return NSNull.NULL;
    }),
    range((self, arguments) -> {
        int argsCount = arguments.size();
        if (argsCount < 1 || argsCount > 3 || !arguments.stream().allMatch(a -> a.getType() == NSData.Type.NUMBER)) {
            throw new ArgumentsError("Invalid arguments");
        }

        int max = ((NSNumber) arguments.get(0)).toInt();
        int min;
        int step;
        if (argsCount > 1) {
            min = max;
            max = ((NSNumber) arguments.get(1)).toInt();
        } else {
            min = 0;
        }
        if (argsCount > 2) {
            step = ((NSNumber) arguments.get(2)).toInt();
        } else {
            step = 1;
        }

        List<NSData<?>> res = new ArrayList<>();
        for (int i = min; i < max; i += step) {
            res.add(NSNumber.from(i));
        }

        return NSList.from(res);
    });

    private final NSNativeFunction function;

    Function(NSNativeFunctionBody body) {
        this.function = new NSNativeFunction(body);
    }

    public NSNativeFunction getFunction() {
        return function;
    }
}
