package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class SelfExpression extends Expression {
    public static SelfExpression parseSelfExpression(Tokens tokens) {
        tokens.require(TokenType.T_KEYWORD_SELF);

        return new SelfExpression();
    }
}
