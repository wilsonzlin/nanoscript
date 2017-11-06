package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.Position;

public class CreateStatement extends Statement {
    private final Identifier identifier;
    private final Expression value;

    public CreateStatement(Position position, Identifier identifier, Expression value) {
        super(position);
        this.identifier = identifier;
        this.value = value;
    }

    public static CreateStatement parseCreateStatement(Tokens tokens) {
        Position position = tokens.require(TokenType.T_KEYWORD_CREATE).getPosition();
        Identifier identifier = Identifier.requireIdentifier(tokens);
        tokens.require(TokenType.T_KEYWORD_AS);
        Expression value = Expression.parseExpression(tokens);

        return new CreateStatement(position, identifier, value);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getValue() {
        return value;
    }
}
