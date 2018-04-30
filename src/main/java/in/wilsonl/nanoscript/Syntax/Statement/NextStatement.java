package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class NextStatement extends Statement {
  public NextStatement (Position position) {
    super(position);
  }

  public static NextStatement parseNextStatement (Tokens tokens) {
    Position position = tokens.require(TokenType.T_KEYWORD_NEXT).getPosition();

    return new NextStatement(position);
  }
}
