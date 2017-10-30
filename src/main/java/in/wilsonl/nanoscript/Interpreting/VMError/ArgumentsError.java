package in.wilsonl.nanoscript.Interpreting.VMError;

import java.util.List;

public class ArgumentsError extends RuntimeError {
    public ArgumentsError(String message) {
        super(message);
    }

    private static ArgumentsError buildMismatchArgumentsError(int expected, int actual) {
        String message = String.format("Function has %d parameter%s but %d argument%s provided", expected, expected != 1 ? "s" : "", actual, actual != 1 ? "s" : "");
        return new ArgumentsError(message);
    }

    public static void assertArgumentsMatch(int expected, List<?> actual) {
        int argsCount = actual.size();
        if (argsCount != expected) {
            throw buildMismatchArgumentsError(expected, argsCount);
        }
    }
}
