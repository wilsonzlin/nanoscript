package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class SelfExpression extends Expression {
    public SelfExpression(Position position) {
        super(position);
    }

    public static SelfExpression parseSelfExpression(Tokens tokens) {
        Position position = tokens.require(TokenType.T_KEYWORD_SELF).getPosition();

        return new SelfExpression(position);
    }
}
