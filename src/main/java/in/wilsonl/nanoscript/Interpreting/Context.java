package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;

public interface Context {
    NSData<?> getContextSymbol(String name);

    boolean setContextSymbol(String name, NSData<?> value);

    default ContextHelper.EvaluationResult evaluateCodeBlockInContext(CodeBlock codeBlock) {
        return ContextHelper.evaluateCodeBlock(this, codeBlock);
    }

    default NSData<?> evaluateExpressionInContext(Expression expression) {
        return ExpressionEvaluator.evaluateExpression(this, expression);
    }
}
