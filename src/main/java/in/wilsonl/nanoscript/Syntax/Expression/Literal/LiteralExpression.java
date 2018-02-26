package in.wilsonl.nanoscript.Syntax.Expression.Literal;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class LiteralExpression<T> extends Expression {
    public static final Map<TokenType, Function<Tokens, LiteralExpression<?>>> PARSERS = _createParsersMap();
    private final Type type;
    private final T value;

    public LiteralExpression(Position position, Type type, T value) {
        super(position);
        this.type = type;
        this.value = value;
    }

    private static Map<TokenType, Function<Tokens, LiteralExpression<?>>> _createParsersMap() {
        Map<TokenType, Function<Tokens, LiteralExpression<?>>> map = new HashMap<>();

        map.put(TokenType.T_LITERAL_BOOLEAN, LiteralBooleanExpression::parseLiteralBooleanExpression);
        map.put(TokenType.T_LITERAL_NULL, LiteralNullExpression::parseLiteralNullExpression);
        map.put(TokenType.T_LITERAL_NUMBER, LiteralNumberExpression::parseLiteralNumberExpression);
        map.put(TokenType.T_LITERAL_STRING, LiteralStringExpression::parseLiteralStringExpression);

        return map;
    }

    public Type getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public enum Type {
        STRING, NUMBER, BOOLEAN, NULL
    }
}
