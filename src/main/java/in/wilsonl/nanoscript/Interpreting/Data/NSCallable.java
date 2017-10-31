package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.ContextHelper;
import in.wilsonl.nanoscript.Interpreting.Evaluator.EvaluationResult;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Parameter;

import java.util.List;

public class NSCallable extends NSData<Object> implements Context {
    // <closure>, <parameters>, and <codeBlock> can be null if it is a native function
    private final ContextHelper closure;
    private final List<Parameter> parameters;
    private final CodeBlock codeBlock;

    private NSCallable(Context parentContext, List<Parameter> parameters, CodeBlock codeBlock) {
        super(Type.CALLABLE, null);
        this.closure = new ContextHelper(parentContext);
        this.parameters = parameters;
        this.codeBlock = codeBlock;
    }

    protected NSCallable() {
        // For NSNativeFunction
        super(Type.CALLABLE, null);
        this.closure = null;
        this.parameters = null;
        this.codeBlock = null;
    }

    public static NSCallable from(Context parentContext, List<Parameter> parameters, CodeBlock codeBlock) {
        return new NSCallable(parentContext, parameters, codeBlock);
    }

    @Override
    public NSData<?> nsCall(List<NSData<?>> arguments) {
        if (closure == null || parameters == null || codeBlock == null) {
            throw new InternalStateError("Non-native callable has null internal state");
        }

        int paramsCount = parameters.size();
        ArgumentsValidator.assertArgumentsMatch(paramsCount, arguments);


        closure.clearSymbols();
        for (int i = 0; i < paramsCount; i++) {
            String name = parameters.get(i).getName().getName();
            closure.createSymbol(name, arguments.get(i));
        }

        EvaluationResult evaluationResult = evaluateCodeBlockInContext(codeBlock);

        if (evaluationResult != null) {
            switch (evaluationResult.getMode()) {
                case BREAK:
                case CONTINUE:
                    throw VMError.from(BuiltinClass.SyntaxError, "Invalid break or next statement");

                case RETURN:
                    return evaluationResult.getValue();

                default:
                    throw new InternalStateError("Unknown evaluation result mode");
            }
        } else {
            return NSNull.NULL;
        }
    }

    @Override
    public NSData<?> getContextSymbol(String name) {
        if (closure == null) {
            throw new InternalStateError("Non-native callable has null internal state");
        }

        return closure.getSymbol(name);
    }

    @Override
    public boolean setContextSymbol(String name, NSData<?> value) {
        if (closure == null) {
            throw new InternalStateError("Non-native callable has null internal state");
        }

        return closure.setSymbol(name, value);
    }

    @Override
    public void createContextSymbol(String name, NSData<?> initialValue) {
        if (closure == null) {
            throw new InternalStateError("Non-native callable has null internal state");
        }

        closure.createSymbol(name, initialValue);
    }
}
