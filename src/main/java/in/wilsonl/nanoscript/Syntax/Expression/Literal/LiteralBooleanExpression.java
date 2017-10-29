package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class LiteralBooleanExpression extends LiteralExpression<Boolean> {
    public LiteralBooleanExpression(boolean value) {
        super(Type.BOOLEAN, value);
    }

    public static LiteralBooleanExpression parseLiteralBooleanExpression(Tokens tokens) {
        return new LiteralBooleanExpression(tokens.require(TokenType.T_LITERAL_BOOLEAN).getValue().equals("true"));
    }
}
