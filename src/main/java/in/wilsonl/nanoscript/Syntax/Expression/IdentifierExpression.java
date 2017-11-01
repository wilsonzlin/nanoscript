package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Identifier;

public class IdentifierExpression extends Expression {
    private final Identifier identifier;

    public IdentifierExpression(Identifier identifier) {
        super(identifier.getPosition());
        this.identifier = identifier;
    }

    public static IdentifierExpression parseIdentifierExpression(Tokens tokens) {
        Identifier identifier = Identifier.requireIdentifier(tokens);
        return new IdentifierExpression(identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
