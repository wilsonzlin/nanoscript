package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSParameter;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSValidatedArguments;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.ContextHelper;
import in.wilsonl.nanoscript.Interpreting.Evaluator.CodeBlockEvaluator;
import in.wilsonl.nanoscript.Interpreting.Evaluator.EvaluationResult;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Syntax.Parameter;

import java.util.List;

// REMEMBER: A callable never loses its context
public class NSVirtualCallable extends NSCallable implements Context {
  private final ContextHelper closure;
  private final CodeBlock body;

  private NSVirtualCallable (ContextHelper closure, NSObject selfValue, ArgumentsValidator parameters, CodeBlock body) {
    super(selfValue, parameters);
    this.closure = closure;
    this.body = body;
  }

  private NSVirtualCallable (Context parentContext, NSObject selfValue, ArgumentsValidator parameters, CodeBlock body) {
    this(new ContextHelper(parentContext), selfValue, parameters, body);
  }

  public static NSVirtualCallable from (Context parentContext, LambdaExpression lambda) {
    List<Parameter> st_params = lambda.getParameters();
    NSParameter[] constraints = new NSParameter[st_params.size()];
    for (int i = 0; i < st_params.size(); i++) {
      Parameter st_p = st_params.get(i);
      constraints[i] = new NSParameter(
        st_p.isOptional(),
        st_p.isVariableLength(),
        st_p.getName().getName(),
        null,
        st_p.getDefaultValue());
    }
    ArgumentsValidator parameters = new ArgumentsValidator(parentContext, constraints);
    return new NSVirtualCallable(parentContext, null, parameters, lambda.getBody());
  }

  @Override
  protected NSData applyBody (NSValidatedArguments arguments) {
    closure.clearSymbols();
    for (NSValidatedArguments.NSValidatedArgument a : arguments) {
      closure.createSymbol(a.getName(), a.getValue());
    }

    EvaluationResult evaluationResult = CodeBlockEvaluator.evaluateCodeBlock(this, body);

    if (evaluationResult != null) {
      switch (evaluationResult.getMode()) {
      case BREAK:
      case NEXT:
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
  protected NSCallable rebindSelf (NSObject to) {
    return new NSVirtualCallable(closure, to, parameters, body);
  }

  @Override
  public NSData getContextSymbol (String name) {
    if (name.equals("self")) {
      return selfValue;
    }
    return closure.getSymbol(name);
  }

  @Override
  public boolean setContextSymbol (String name, NSData value) {
    return closure.setSymbol(name, value);
  }

  @Override
  public void createContextSymbol (String name, NSData initialValue) {
    closure.createSymbol(name, initialValue);
  }
}
