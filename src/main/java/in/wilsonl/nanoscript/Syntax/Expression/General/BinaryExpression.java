package in.wilsonl.nanoscript.Syntax.Expression.General;

import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Operator;

public class BinaryExpression extends Expression {
    private final Expression lhs;
    private final Operator operator;
    private final Expression rhs;

    public BinaryExpression(Expression lhs, Operator operator, Expression rhs) {
        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public Expression getLHS() {
        return lhs;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getRHS() {
        return rhs;
    }
}
