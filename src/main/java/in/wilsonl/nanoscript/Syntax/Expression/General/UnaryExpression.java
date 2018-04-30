package in.wilsonl.nanoscript.Syntax.Expression.General;

import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Utils.Position;

public class UnaryExpression extends Expression {
  private final Operator operator;
  private final Expression operand;

  public UnaryExpression (Position position, Operator operator, Expression operand) {
    super(position);
    this.operator = operator;
    this.operand = operand;
  }

  public Operator getOperator () {
    return operator;
  }

  public Expression getOperand () {
    return operand;
  }
}
