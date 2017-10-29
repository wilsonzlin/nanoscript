package in.wilsonl.nanoscript.Syntax.Expression.General;

import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Operator;

public class UnaryExpression extends Expression {
    private final Operator operator;
    private final Expression operand;

    public UnaryExpression(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }
}
