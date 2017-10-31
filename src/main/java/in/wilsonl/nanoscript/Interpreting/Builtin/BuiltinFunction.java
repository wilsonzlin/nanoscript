package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.ArgumentsValidator.Parameter;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;

import java.util.ArrayList;
import java.util.List;

public enum BuiltinFunction {
    print(null, (arguments) -> {
        if (arguments.size() == 0) {
            System.out.println();
        } else {
            for (int i = 0; i < arguments.size() - 1; i++) {
                System.out.print(arguments.get(i).nsToString().getRawValue() + ", ");
            }
            System.out.println(arguments.get(arguments.size() - 1).nsToString().getRawValue());
        }

        return NSNull.NULL;
    }),
    range(new ArgumentsValidator(new Parameter[]{
            new Parameter(NSData.Type.NUMBER),
            null,
            new Parameter(NSData.Type.NUMBER),
            new Parameter(NSData.Type.NUMBER)
    }), (arguments) -> {
        int argsCount = arguments.size();

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
    }),
    str(ArgumentsValidator.ONE, (arguments) -> arguments.get(0).nsToString());

    private final NSNativeFunction function;

    BuiltinFunction(ArgumentsValidator validator, SelflessBody body) {
        this.function = new NSNativeFunction((__, arguments) -> {
            if (validator != null) {
                validator.validate(arguments);
            }
            return body.run(arguments);
        });
    }

    public NSNativeFunction getFunction() {
        return function;
    }

    private interface SelflessBody {
        NSData<?> run(List<NSData<?>> arguments);
    }
}
