package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class LiteralNullExpression extends LiteralExpression<Object> {
    public LiteralNullExpression() {
        super(Type.NULL, null);
    }

    public static LiteralNullExpression parseLiteralNullExpression(Tokens tokens) {
        tokens.require(TokenType.T_LITERAL_NULL);
        return new LiteralNullExpression();
    }
}
