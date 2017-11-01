package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.SetOnce;

public class ThrowStatement extends Statement {
    private final SetOnce<Expression> value = new SetOnce<>();

    public ThrowStatement(Position position) {
        super(position);
    }

    public static Statement parseThrowStatement(Tokens tokens) {
        Position position = tokens.require(TokenType.T_KEYWORD_THROW).getPosition();
        ThrowStatement throwStatement = new ThrowStatement(position);

        throwStatement.setValue(Expression.parseExpression(tokens));

        return throwStatement;
    }

    public Expression getValue() {
        return value.get();
    }

    public void setValue(Expression value) {
        this.value.set(value);
    }
}
