package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class LiteralNullExpression extends LiteralExpression<Object> {
  public LiteralNullExpression (Position position) {
    super(position, Type.NULL, null);
  }

  public static LiteralNullExpression parseLiteralNullExpression (Tokens tokens) {
    Position position = tokens.require(TokenType.T_LITERAL_NULL).getPosition();
    return new LiteralNullExpression(position);
  }
}
