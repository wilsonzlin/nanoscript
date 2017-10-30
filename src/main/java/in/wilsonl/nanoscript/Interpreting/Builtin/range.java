package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNativeFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.VMError.ArgumentsError;

import java.util.ArrayList;
import java.util.List;

public class range {
    public static final NSNativeFunction range = createRangeFunction();

    private static NSNativeFunction createRangeFunction() {
        return new NSNativeFunction() {
            @Override
            public NSData<?> applyCall(List<NSData<?>> arguments) {
                int argsCount = arguments.size();
                if (argsCount < 1 || argsCount > 3 || !arguments.stream().allMatch(a -> a.getType() == Type.NUMBER)) {
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
            }
        };
    }
}
