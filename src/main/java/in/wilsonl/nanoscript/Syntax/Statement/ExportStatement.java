package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;

public class ExportStatement extends Statement {
    private final Expression value;
    private final Identifier name; // Can be null

    public ExportStatement(Expression value, Identifier name) {
        this.value = value;
        this.name = name;
    }

    public static Statement parseExportStatement(Tokens tokens) {
        tokens.require(TokenType.T_KEYWORD_EXPORT);

        Expression value = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_KEYWORD_AS));

        tokens.require(TokenType.T_KEYWORD_AS);

        Identifier name = Identifier.requireIdentifier(tokens);

        return new ExportStatement(value, name);
    }

    public Expression getValue() {
        return value;
    }

    public Identifier getName() {
        return name;
    }
}
