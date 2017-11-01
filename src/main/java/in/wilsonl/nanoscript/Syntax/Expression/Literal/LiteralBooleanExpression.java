package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class LiteralBooleanExpression extends LiteralExpression<Boolean> {
    public LiteralBooleanExpression(Position position, boolean value) {
        super(position, Type.BOOLEAN, value);
    }

    public static LiteralBooleanExpression parseLiteralBooleanExpression(Tokens tokens) {
        Token token = tokens.require(TokenType.T_LITERAL_BOOLEAN);
        return new LiteralBooleanExpression(token.getPosition(), token.getValue().equals("true"));
    }
}
