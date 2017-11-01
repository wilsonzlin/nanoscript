package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ConditionalBranchesExpression extends Expression {

    private static final AcceptableTokenTypes BRANCH_VALUE_END_DELIMITER = new AcceptableTokenTypes(
            T_KEYWORD_ELIF,
            T_KEYWORD_ELSE,
            T_KEYWORD_ENDIF
    );

    private final List<Branch> conditionalBranches = new ROList<>();
    // `if` expressions must have `else` branch
    private final SetOnce<Expression> finalBranchValue = new SetOnce<>();

    public ConditionalBranchesExpression(Position position) {
        super(position);
    }

    public static ConditionalBranchesExpression parseConditionalBranchesExpression(Tokens tokens) {
        Position position = tokens.require(T_KEYWORD_IF).getPosition();
        ConditionalBranchesExpression branches = new ConditionalBranchesExpression(position);

        boolean done = false;
        do {
            Expression condition = Expression.parseExpression(tokens, new AcceptableTokenTypes(T_COLON));
            tokens.require(T_COLON);
            Expression value = Expression.parseExpression(tokens, BRANCH_VALUE_END_DELIMITER);

            branches.addBranch(condition, value);

            TokenType nextToken = tokens.peekType();

            switch (nextToken) {
                case T_KEYWORD_ELSEIF:
                    tokens.skip();
                    break;

                case T_KEYWORD_OTHERWISE:
                case T_KEYWORD_ENDIF:
                    done = true;
                    break;

                default:
                    throw new InternalStateError("Unknown token type after parsing conditional branch expression body: " + nextToken);
            }
        } while (!done);

        tokens.require(T_KEYWORD_ELSE);
        tokens.require(T_COLON);
        Expression value = Expression.parseExpression(tokens, new AcceptableTokenTypes(T_KEYWORD_ENDIF));

        branches.setFinalBranch(value);

        tokens.require(T_KEYWORD_ENDIF);

        return branches;
    }

    public List<Branch> getConditionalBranches() {
        return conditionalBranches;
    }

    public Expression getFinalBranchValue() {
        return finalBranchValue.get();
    }

    public void addBranch(Expression condition, Expression value) {
        conditionalBranches.add(new Branch(condition, value));
    }

    public void setFinalBranch(Expression value) {
        finalBranchValue.set(value);
    }

    public static class Branch {
        private final Expression condition;
        private final Expression value;

        private Branch(Expression condition, Expression value) {
            this.condition = condition;
            this.value = value;
        }

        public Expression getCondition() {
            return condition;
        }

        public Expression getValue() {
            return value;
        }
    }

}
