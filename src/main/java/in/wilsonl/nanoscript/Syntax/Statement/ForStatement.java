package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ForStatement extends Statement {
    private static final AcceptableTokenTypes INIT_ITER_EXP_END_DELIMITER = new AcceptableTokenTypes(T_COMMA, T_KEYWORD_DO);

    private final List<Iterable> iterables = new ROList<>();
    private final SetOnce<CodeBlock> body = new SetOnce<>();

    public ForStatement(Position position) {
        super(position);
    }

    public static ForStatement parseForStatement(Tokens tokens) {
        Position position = tokens.require(T_KEYWORD_FOR).getPosition();
        ForStatement forExpression = new ForStatement(position);
        CodeBlock body;

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

    public List<Iterable> getIterables() {
        return iterables;
    }

    public CodeBlock getBody() {
        return body.get();
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

        public Identifier getFormalParameterName() {
            return formalParameterName;
        }

        public Expression getExpression() {
            return expression;
        }
    }
}
