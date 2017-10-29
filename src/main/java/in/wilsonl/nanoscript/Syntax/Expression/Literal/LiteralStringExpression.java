package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class LiteralStringExpression extends LiteralExpression<String> {
    public LiteralStringExpression(String value) {
        super(Type.STRING, value);
    }

    public static LiteralStringExpression parseLiteralStringExpression(Tokens tokens) {
        return new LiteralStringExpression(tokens.require(TokenType.T_LITERAL_STRING).getValue());
    }
}
