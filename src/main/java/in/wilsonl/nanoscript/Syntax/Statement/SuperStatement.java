package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.CallExpression;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Utils.Position;

public class SuperStatement extends Statement {
    private final Reference parent; // Can be null
    private final CallExpression.Arguments arguments;

    public SuperStatement(Position position, Reference parent, CallExpression.Arguments arguments) {
        super(position);
        this.parent = parent;
        this.arguments = arguments;
    }

    public static SuperStatement parseSuperStatement(Tokens tokens) {
        Position position = tokens.require(TokenType.T_KEYWORD_SUPER).getPosition();

        Reference parent;
        if (tokens.isNext(TokenType.T_IDENTIFIER)) {
            parent = Reference.parseReference(tokens);
        } else {
            parent = null;
        }

        tokens.require(TokenType.T_PARENTHESIS_LEFT);

        CallExpression.Arguments arguments = CallExpression.parseCallExpressionArguments(tokens);

        tokens.require(TokenType.T_PARENTHESIS_RIGHT);

        return new SuperStatement(position, parent, arguments);
    }

    public Reference getParent() {
        return parent;
    }

    public CallExpression.Arguments getArguments() {
        return arguments;
    }
}
