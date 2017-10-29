package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class LookupExpression extends Expression {
    private final boolean nullSafe;
    private final Expression source;
    private final Terms terms;

    public LookupExpression(boolean nullSafe, Expression source, Terms terms) {
        this.nullSafe = nullSafe;
        this.source = source;
        this.terms = terms;
    }

    public static Terms parseLookupExpressionTerms(Tokens tokens) {
        Terms terms = new Terms();

        do {
            Expression termExpr = parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_COMMA, TokenType.T_SQUARE_BRACKET_RIGHT));
            terms.addTerm(termExpr);
        } while (tokens.skipIfNext(TokenType.T_COMMA));

        return terms;
    }

    public static class Terms {
        private final List<Expression> terms = new ROList<>();

        public void addTerm(Expression t) {
            terms.add(t);
        }
    }
}
