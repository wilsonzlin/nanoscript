package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.Position;

public class SetStatement extends Statement {
  private final Expression target;
  private final Expression value;

  public SetStatement (Position position, Expression target, Expression value) {
    super(position);
    this.target = target;
    this.value = value;
  }

  public static SetStatement parseSetStatement (Tokens tokens) {
    Position position = tokens.require(TokenType.T_KEYWORD_SET).getPosition();
    Expression identifier = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_KEYWORD_TO));
    tokens.require(TokenType.T_KEYWORD_TO);
    Expression value = Expression.parseExpression(tokens);

    return new SetStatement(position, identifier, value);
  }

  public Expression getTarget () {
    return target;
  }

  public Expression getValue () {
    return value;
  }
}
