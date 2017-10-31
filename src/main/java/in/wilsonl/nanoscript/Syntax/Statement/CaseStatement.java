package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class CaseStatement extends Statement {
    private static final AcceptableTokenTypes OPTION_DELIMITER = new AcceptableTokenTypes(T_KEYWORD_WHEN, T_KEYWORD_OTHERWISE, T_KEYWORD_CASE_END);
    private static final AcceptableTokenTypes OPTION_TYPE = new AcceptableTokenTypes(T_KEYWORD_WHEN, T_KEYWORD_OTHERWISE);

    // Options are ordered
    private final List<Option> options = new ROList<>();
    private final SetOnce<Expression> target = new SetOnce<>();
    private boolean hasCatchAll = false;

    public static CaseStatement parseCaseStatement(Tokens tokens) {
        CaseStatement caseStatement = new CaseStatement();

        tokens.require(T_KEYWORD_CASE);

        Expression target = Expression.parseExpression(tokens, OPTION_DELIMITER);
        caseStatement.setTarget(target);

        while (tokens.peekType() != T_KEYWORD_CASE_END) {
            TokenType optionType = tokens.require(OPTION_TYPE).getType();
            Expression condition;

            switch (optionType) {
                case T_KEYWORD_WHEN:
                    condition = Expression.parseExpression(tokens, new AcceptableTokenTypes(T_KEYWORD_THEN));
                    tokens.require(T_KEYWORD_THEN);
                    break;

                case T_KEYWORD_OTHERWISE:
                    condition = null;
                    break;

                default:
                    throw new InternalStateError("Unknown case expression option type: " + optionType);
            }

            CodeBlock body = CodeBlock.parseCodeBlock(tokens, OPTION_DELIMITER);

            Option option = new Option(condition, body);
            try {
                caseStatement.pushOption(option);
            } catch (IllegalStateException iae) {
                throw tokens.constructMalformedSyntaxException("Catch-all option for case expression already exists");
            }
        }

        tokens.require(T_KEYWORD_CASE_END);

        return caseStatement;
    }

    public void pushOption(Option option) {
        if (option.isCatchAll()) {
            if (hasCatchAll) {
                throw new IllegalStateException("A catch-all option already exists");
            }
            hasCatchAll = true;
        }
        options.add(option);
    }

    public List<Option> getOptions() {
        return options;
    }

    public Expression getTarget() {
        return target.get();
    }

    public void setTarget(Expression target) {
        this.target.set(target);
    }

    public static class Option {
        private final Expression condition; // Can be null
        private final CodeBlock body;

        public Option(Expression condition, CodeBlock body) {
            this.condition = condition;
            this.body = body;
        }

        public boolean isCatchAll() {
            return this.condition == null;
        }

        public Expression getCondition() {
            return condition;
        }

        public CodeBlock getBody() {
            return body;
        }
    }
}
