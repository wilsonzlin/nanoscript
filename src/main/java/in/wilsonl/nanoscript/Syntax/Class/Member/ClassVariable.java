package in.wilsonl.nanoscript.Syntax.Class.Member;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Variable;
import in.wilsonl.nanoscript.Utils.SetOnce;

public class ClassVariable extends ClassMember {
    private final SetOnce<Variable> variable = new SetOnce<>();

    public static ClassVariable parseClassVariable(Tokens tokens) {
        ClassVariable oomlClassVariable = new ClassVariable();
        Variable oomlVariable = new Variable();
        tokens.require(TokenType.T_KEYWORD_VARIABLE);
        if (tokens.skipIfNext(TokenType.T_COLON)) {
            oomlClassVariable.isStatic(tokens.skipIfNext(TokenType.T_KEYWORD_STATIC));
        } else {
            oomlClassVariable.isStatic(false);
        }

        oomlVariable.setName(Identifier.requireIdentifier(tokens));

        tokens.require(TokenType.T_KEYWORD_AS);
        oomlVariable.setInitialiser(Expression.parseExpression(tokens));

        oomlClassVariable.setVariable(oomlVariable);
        return oomlClassVariable;
    }

    public Variable getVariable() {
        return variable.get();
    }

    public void setVariable(Variable variable) {
        this.variable.set(variable);
    }
}
