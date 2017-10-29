package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class NextStatement extends Statement {
    public static NextStatement parseNextStatement(Tokens tokens) {
        tokens.require(TokenType.T_KEYWORD_NEXT);

        return new NextStatement();
    }
}
