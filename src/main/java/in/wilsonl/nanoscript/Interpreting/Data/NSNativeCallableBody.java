package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.Map;

public interface NSNativeCallableBody {
    NSData function(NSObject self, Map<String, NSData> arguments);
}
