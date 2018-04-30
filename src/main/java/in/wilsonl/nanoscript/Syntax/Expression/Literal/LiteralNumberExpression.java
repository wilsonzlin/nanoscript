package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class LiteralNumberExpression extends LiteralExpression<Double> {
  public LiteralNumberExpression (Position position, double value) {
    super(position, Type.NUMBER, value);
  }

  public static LiteralNumberExpression parseLiteralNumberExpression (Tokens tokens) {
    Token token = tokens.require(TokenType.T_LITERAL_NUMBER);
    return new LiteralNumberExpression(token.getPosition(), Double.parseDouble(token.getValue()));
  }
}
