package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class BreakStatement extends Statement {
    public static BreakStatement parseBreakStatement(Tokens tokens) {
        tokens.require(TokenType.T_KEYWORD_BREAK);

        return new BreakStatement();
    }
}
