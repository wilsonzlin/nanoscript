package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;

public class ExpressionStatement extends Statement {
    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public static ExpressionStatement parseExpressionStatement(Tokens tokens) {
        return new ExpressionStatement(Expression.parseExpression(tokens));
    }
}
