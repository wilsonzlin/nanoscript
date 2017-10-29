package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Identifier;

public class IdentifierExpression extends Expression {
    private final Identifier identifier;

    public IdentifierExpression(Identifier identifier) {
        this.identifier = identifier;
    }

    public static IdentifierExpression parseIdentifierExpression(Tokens tokens) {
        return new IdentifierExpression(Identifier.requireIdentifier(tokens));
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
