package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.ContextHelper;
import in.wilsonl.nanoscript.Interpreting.VMError.ArgumentsError;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Parameter;

import java.util.List;

public class NSCallable extends NSData<Object> implements Context {
    private final ContextHelper context;
    private final List<Parameter> parameters;
    private final CodeBlock codeBlock;

    private NSCallable(Context parentContext, List<Parameter> parameters, CodeBlock codeBlock) {
        super(Type.CALLABLE, null);
        this.context = new ContextHelper(parentContext);
        this.parameters = parameters;
        this.codeBlock = codeBlock;
    }

    public static NSCallable from(Context parentContext, List<Parameter> parameters, CodeBlock codeBlock) {
        return new NSCallable(parentContext, parameters, codeBlock);
    }

    @Override
    public NSData<?> applyCall(List<NSData<?>> arguments) {
        if (arguments.size() != parameters.size()) {
            throw new ArgumentsError(String.format("Function has %d parameters but %d arguments provided", parameters.size(), arguments.size()));
        }

        context.clear();
        for (int i = 0; i < parameters.size(); i++) {
            String name = parameters.get(i).getName().getName();
            context.setSymbol(name, arguments.get(i));
        }

        ContextHelper.EvaluationResult evaluationResult = evaluateCodeBlockInContext(codeBlock);

        // TODO
    }

    @Override
    public NSData<?> getContextSymbol(String name) {
        return context.getSymbol(name);
    }

    @Override
    public boolean setContextSymbol(String name, NSData<?> value) {
        return context.setSymbol(name, value);
    }
}
