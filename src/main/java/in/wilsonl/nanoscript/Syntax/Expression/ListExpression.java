package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class ListExpression extends Expression {
    private final List<Expression> values = new ROList<>();

    public ListExpression(Position position) {
        super(position);
    }

    public static ListExpression parseListExpression(Tokens tokens) {
        Position position = tokens.require(TokenType.T_SQUARE_BRACKET_LEFT).getPosition();
        ListExpression list = new ListExpression(position);

        do {
            if (tokens.isNext(TokenType.T_SQUARE_BRACKET_RIGHT)) {
                break;
            }

            list.addValue(Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_COMMA, TokenType.T_SQUARE_BRACKET_RIGHT)));
        } while (tokens.skipIfNext(TokenType.T_COMMA));

        tokens.require(TokenType.T_SQUARE_BRACKET_RIGHT);

        return list;
    }

    public void addValue(Expression value) {
        values.add(value);
    }

    public List<Expression> getValues() {
        return values;
    }
}
