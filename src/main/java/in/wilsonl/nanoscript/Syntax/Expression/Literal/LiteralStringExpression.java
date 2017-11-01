package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class LiteralStringExpression extends LiteralExpression<String> {
    public LiteralStringExpression(Position position, String value) {
        super(position, Type.STRING, value);
    }

    public static LiteralStringExpression parseLiteralStringExpression(Tokens tokens) {
        Token token = tokens.require(TokenType.T_LITERAL_STRING);
        return new LiteralStringExpression(token.getPosition(), token.getValue());
    }
}
