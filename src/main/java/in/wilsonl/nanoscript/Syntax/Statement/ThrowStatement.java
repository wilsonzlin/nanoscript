package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.SetOnce;

public class ThrowStatement extends Statement {
    private final SetOnce<Expression> value = new SetOnce<>();

    public static Statement parseThrowStatement(Tokens tokens) {
        ThrowStatement throwStatement = new ThrowStatement();

        tokens.require(TokenType.T_KEYWORD_THROW);

        throwStatement.setValue(Expression.parseExpression(tokens));

        return throwStatement;
    }

    public void setValue(Expression value) {
        this.value.set(value);
    }

    public Expression getValue() {
        return value.get();
    }
}
