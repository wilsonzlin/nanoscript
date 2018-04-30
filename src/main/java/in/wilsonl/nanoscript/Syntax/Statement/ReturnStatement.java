package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.Position;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ReturnStatement extends Statement {
  private static final AcceptableTokenTypes DELIMITER = new AcceptableTokenTypes(T_KEYWORD_ENDIF, T_KEYWORD_CASE_END, T_KEYWORD_FOR_END, T_KEYWORD_FUNCTION_END, T_KEYWORD_METHOD_END);
  private final Expression value; // Can be null

  public ReturnStatement (Position position, Expression value) {
    super(position);
    this.value = value;
  }

  public static ReturnStatement parseReturnStatement (Tokens tokens) {
    Position position = tokens.require(T_KEYWORD_RETURN).getPosition();

    Expression value;
    if (DELIMITER.has(tokens.peek())) {
      value = null;
    } else {
      value = Expression.parseExpression(tokens, DELIMITER);
    }

    return new ReturnStatement(position, value);
  }

  public Expression getValue () {
    return value;
  }
}
