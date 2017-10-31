package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.ArgumentsValidator;

import java.util.List;

public interface NSNativeFunctionBody extends NSInstanceMethodSource, NSConstructorSource {
    static NSNativeFunctionBody withValidator(ArgumentsValidator validator, NSNativeFunctionBody body) {
        return (self, arguments) -> {
            validator.validate(arguments);
            return body.run(self, arguments);
        };
    }

    NSData<?> run(NSObject self, List<NSData<?>> arguments);
}
