package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.ROSet;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.Set;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ForStatement extends Statement {
    private static final AcceptableTokenTypes INIT_ITER_EXP_END_DELIMITER = new AcceptableTokenTypes(T_COMMA, T_KEYWORD_DO);
    private final Set<Iterable> iterables = new ROSet<>();
    private final SetOnce<CodeBlock> body = new SetOnce<>();

    /*
     *   for-loops are not usually expressions; in almost all cases, they can only be used
     *   as expressions if they have a `put` statement.
     *
     *   The only special time for-loop bodies don't have to be delimited with `do` and
     *   `endfor` is when it only has one statement, and that statement is a `put`
     *   statement. This is to simplify syntax when the for-loop is used as an expression.
     */
    public static ForStatement parseForStatement(Tokens tokens) {
        ForStatement forExpression = new ForStatement();
        CodeBlock body;

        tokens.require(T_KEYWORD_FOR);
        do {
            Identifier formalParameterName = Identifier.requireIdentifier(tokens);

            tokens.require(T_KEYWORD_IN);
            Expression expression = Expression.parseExpression(tokens, INIT_ITER_EXP_END_DELIMITER);

            forExpression.addIterable(new Iterable(formalParameterName, expression));
        } while (tokens.skipIfNext(T_COMMA));

        tokens.require(T_KEYWORD_DO);
        body = CodeBlock.parseCodeBlock(tokens, TokenType.T_KEYWORD_FOR_END);
        tokens.require(T_KEYWORD_FOR_END);

        forExpression.setBody(body);

        return forExpression;
    }

    public void setBody(CodeBlock body) {
        this.body.set(body);
    }

    public void addIterable(Iterable iterable) {
        // Duplicates are possible and allowed
        iterables.add(iterable);
    }

    public static class Iterable {
        private final Identifier formalParameterName;
        private final Expression expression;

        public Iterable(Identifier formalParameterName, Expression expression) {
            this.formalParameterName = formalParameterName;
            this.expression = expression;
        }
    }
}
