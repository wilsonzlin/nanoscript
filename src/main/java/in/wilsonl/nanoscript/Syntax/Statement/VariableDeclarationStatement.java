package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Variable;

public class VariableDeclarationStatement extends Statement {
    private final Variable variable;

    public VariableDeclarationStatement(Variable variable) {
        super(variable.getName().getPosition());
        this.variable = variable;
    }

    public static VariableDeclarationStatement parseVariableDeclarationStatement(Tokens tokens) {
        Variable variable = new Variable();

        Identifier identifier = Identifier.requireIdentifier(tokens);
        variable.setName(identifier);

        tokens.require(TokenType.T_INITIALISE);

        variable.setInitialiser(Expression.parseExpression(tokens));

        return new VariableDeclarationStatement(variable);
    }

    public Variable getVariable() {
        return variable;
    }
}
