package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.List;

public interface NSNativeFunctionBody extends NSInstanceMethodSource, NSConstructorSource {
    NSData<?> run(NSObject self, List<NSData<?>> arguments);
}
