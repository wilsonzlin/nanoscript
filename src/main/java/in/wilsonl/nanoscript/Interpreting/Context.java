package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Evaluator.CodeBlockEvaluator;
import in.wilsonl.nanoscript.Interpreting.VMError.UnsupportedOperationError;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;

import static in.wilsonl.nanoscript.Interpreting.Evaluator.CodeBlockEvaluator.evaluateCodeBlock;
import static in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator.evaluateExpression;

public interface Context {
    NSData<?> getContextSymbol(String name);

    boolean setContextSymbol(String name, NSData<?> value);

    default void createContextSymbol(String name, NSData<?> initialValue) {
        throw new UnsupportedOperationError("Variables can't be declared here");
    }

    default CodeBlockEvaluator.EvaluationResult evaluateCodeBlockInContext(CodeBlock codeBlock) {
        return evaluateCodeBlock(this, codeBlock);
    }

    default NSData<?> evaluateExpressionInContext(Expression expression) {
        return evaluateExpression(this, expression);
    }
}
