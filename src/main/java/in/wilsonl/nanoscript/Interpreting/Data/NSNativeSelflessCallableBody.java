package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.Map;

public interface NSNativeSelflessCallableBody extends NSNativeCallableBody {
    @Override
    default NSData function(NSObject self, Map<String, NSData> arguments) {
        return selflessFunction(arguments);
    }

    NSData selflessFunction(Map<String, NSData> arguments);
}
