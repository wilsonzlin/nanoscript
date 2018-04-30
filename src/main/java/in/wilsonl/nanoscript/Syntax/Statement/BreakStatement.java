package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class BreakStatement extends Statement {
  public BreakStatement (Position position) {
    super(position);
  }

  public static BreakStatement parseBreakStatement (Tokens tokens) {
    Position position = tokens.require(TokenType.T_KEYWORD_BREAK).getPosition();

    return new BreakStatement(position);
  }
}
