package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class LiteralNumberExpression extends LiteralExpression<Double> {
    public LiteralNumberExpression(double value) {
        super(Type.NUMBER, value);
    }

    public static LiteralNumberExpression parseLiteralNumberExpression(Tokens tokens) {
        return new LiteralNumberExpression(Double.parseDouble(tokens.require(TokenType.T_LITERAL_NUMBER).getValue()));
    }
}
